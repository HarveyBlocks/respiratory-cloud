package org.harvey.respiratory.cloud.disease.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.DiseaseDiagnosisIntermediationService;
import org.harvey.respiratory.cloud.api.service.DiseaseService;
import org.harvey.respiratory.cloud.common.advice.cache.MultiRedisCacheAdvice;
import org.harvey.respiratory.cloud.common.advice.cache.RedisCacheAdvice;
import org.harvey.respiratory.cloud.common.advice.cache.bind.MultipleQueryBind;
import org.harvey.respiratory.cloud.common.advice.cache.executor.QueryBasisHaving;
import org.harvey.respiratory.cloud.common.constants.KeyGenerator;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Disease;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;
import org.harvey.respiratory.cloud.common.utils.JacksonUtil;
import org.harvey.respiratory.cloud.disease.server.dao.DiseaseMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;


/**
 * Disease的Service实现
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:32
 */
@Slf4j
@Service
@NonNull
@org.apache.dubbo.config.annotation.DubboService
public class DiseaseServiceImpl extends ServiceImpl<DiseaseMapper, Disease> implements DiseaseService {
    @DubboReference
    private DiseaseDiagnosisIntermediationService diseaseDiagnosisIntermediationService;

    @Resource
    private RedisCacheAdvice redisCacheAdvice;
    @Resource
    private MultiRedisCacheAdvice multiRedisCacheAdvice;
    private final MultipleQueryBind<Integer, Disease> selectByIdsQueryBind;
    private final MultipleQueryBind<Long, IntegerIdPage> selectByPageQueryBind;

    public DiseaseServiceImpl() {
        // selectByIdsQueryBind
        KeyGenerator<Integer> selectByIdsKeyGenerator = KeyGenerator.<Integer>ofPrefix(RedisConstants.Disease.ON_ID)
                .addPrefix(RedisConstants.QUERY_KEY_PREFIX);
        selectByIdsQueryBind = new MultipleQueryBind<>(selectByIdsKeyGenerator, super::listByIds, true);

        // selectByPageQueryBind
        KeyGenerator<Long> objectKeyGenerator = KeyGenerator.<Long>ofPrefix(RedisConstants.Disease.ON_PAGE)
                .addPrefix(RedisConstants.QUERY_KEY_PREFIX);
        selectByPageQueryBind = new MultipleQueryBind<>(objectKeyGenerator,
                this::selectIdsByPageSlow, false
        );
    }

    @NotNull
    private List<IntegerIdPage> selectIdsByPageSlow(List<Long> redisCurrentList) {
        return redisCurrentList.stream().map(current -> {
            Page<Disease> page = new Page<>(current, RedisConstants.DEFAULT_LIMIT);
            List<Integer> ids = super.lambdaQuery()
                    .select(Disease::getId)
                    .page(page)
                    .getRecords()
                    .stream()
                    .map(Disease::getId)
                    .collect(Collectors.toList());
            return new IntegerIdPage(current, ids);
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteById(int id) {
        boolean removed = super.removeById(id);
        if (removed) {
            log.debug("正常删除{}的疾病", id);
        }
    }

    @Override
    @NonNull
    public List<Disease> selectByVisitDoctor(long visitId) {
        // 可能不是热门数据
        List<Integer> idsOfVisit = diseaseDiagnosisIntermediationService.selectDiseaseByVisit(visitId);
        // 用Redis增强
        return selectByIds(idsOfVisit);
    }

    private List<Disease> selectByIds(List<Integer> ids) {
        return multiRedisCacheAdvice.adviceOnValue(ids, selectByIdsQueryBind);
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JacksonUtil jacksonUtil;

    @Deprecated
    private List<Disease> selectByIdsSimply(List<Integer> ids) {
        // 制造缓存key
        List<String> idKeyList = ids.stream().map(id -> RedisConstants.Disease.ON_ID + id).collect(Collectors.toList());
        // 从缓存中读
        List<String> resultsInCache = stringRedisTemplate.opsForValue().multiGet(idKeyList);
        // 一般不会是null吧?
        if (resultsInCache == null) {
            throw new ServerException("unknown redis exception for result list is null");
        }
        // 获取结果是null的索引, 表示在缓存中不存在

        List<Integer> earnIdsFromDb = IntStream.range(0, ids.size())
                .filter(i -> resultsInCache.get(i) ==
                             null) // 如果是 RedisConstants.FAKE_DATA_FOR_NULL, 表示假数据, 如果是null, 表示是需要从数据库查
                .map(ids::get)
                .boxed()
                .collect(Collectors.toList());
        // 缓存中不存在的键, 需要从数据库里查
        if (earnIdsFromDb.isEmpty()) {
            // 不需要从数据库读
            return resultsInCache.stream()
                    .map(s -> s.isEmpty() ? null : jacksonUtil.toBean(s, Disease.class))
                    .collect(Collectors.toList());
        }
        // 慢读
        List<Disease> diseasesInDb = super.listByIds(earnIdsFromDb);
        // 转换成id-entity的map结构
        Map<Integer, Disease> diseasesInDbMap = diseasesInDb.stream().collect(Collectors.toMap(Disease::getId, d -> d));
        // 准备存入缓存的数据
        Map<String, String> multiSetMap = earnIdsFromDb.stream()
                .collect(Collectors.toMap(
                        earnIdFromDb -> RedisConstants.Disease.ON_ID + earnIdFromDb/*build key*/,
                        earnIdFromDb -> {
                            Disease bean = diseasesInDbMap.get(earnIdFromDb);
                            // 数据库也不存在? 那就是假数据
                            return bean == null ? RedisConstants.FAKE_DATA_FOR_NULL/*假数据*/ :
                                    jacksonUtil.toJsonStr(bean);
                        }
                ));
        // 存入缓存
        stringRedisTemplate.opsForValue().multiSet(multiSetMap);
        // 批量构建时间
        long ttl = 12;
        Set<String> newKeysIntoCache = multiSetMap.keySet();
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            newKeysIntoCache.forEach(key -> connection.expire(key.getBytes(), ttl/*s*/));
            return null;
        });
        // 构造最终结果
        List<Disease> result = new ArrayList<>();
        for (int i = 0; i < resultsInCache.size(); i++) {
            String resultInCache = resultsInCache.get(i);
            if (resultInCache == null) {
                // 不存在, 从数据库中获取的数据
                Integer id = ids.get(i);
                Disease diseaseInDb = diseasesInDbMap.get(id);
                result.add(diseaseInDb);
            } else if (resultInCache.isEmpty()) {
                // 不存在id的假数据
                result.add(null);
            } else {
                result.add(jacksonUtil.toBean(resultInCache));
            }
        }
        return result;
    }

    @Override
    @NonNull
    public Disease selectById(int id) {
        String queryKey = RedisConstants.QUERY_KEY_PREFIX + RedisConstants.Disease.ON_ID + id;
        Disease disease = redisCacheAdvice.adviceOnValue(queryKey, () -> super.getById(id)/*慢查询*/, true);
        if (disease != null) {
            return disease;
        } else {
            throw new ResourceNotFountException("not found disease: " + id);
        }
    }

    @AllArgsConstructor
    @Data
    private static class IntegerIdPage implements QueryBasisHaving<Long> {
        private final Long current;
        private final List<Integer> ids;

        @Override
        public Long getQueryBasis() {
            return current;
        }
    }

    @Override
    @NonNull
    public List<Disease> selectByPage(Page<Disease> page) {
        //  分页的缓存
        long current = page.getCurrent();
        long size = page.getSize();
        long start = (current - 1) * size;
        // 需要在缓存中定位的页
        long redisCurrentStart = start / RedisConstants.DEFAULT_LIMIT + 1;
        long redisCurrentEnd = (start + size) / RedisConstants.DEFAULT_LIMIT + 1;
        // 需要查询的页码是...
        // 包含两头的
        List<Long> redisCurrentList = LongStream.range(redisCurrentStart, redisCurrentEnd + 1)
                .boxed()
                .collect(Collectors.toList());
        // 获取到的页面集
        List<IntegerIdPage> integerIdPageList = adviceOnRedisSelectByPageQueryBind(redisCurrentList);
        // 需要的id
        List<Integer> ids = integerIdPageList.stream()
                .map(IntegerIdPage::getIds)
                .flatMap(List::stream)
                .skip(start - start / RedisConstants.DEFAULT_LIMIT * RedisConstants.DEFAULT_LIMIT)
                .limit(size)
                .collect(Collectors.toList());
        return selectByIds(ids);
    }

    private List<IntegerIdPage> adviceOnRedisSelectByPageQueryBind(List<Long> redisCurrentList) {
        return multiRedisCacheAdvice.adviceOnValue(redisCurrentList, selectByPageQueryBind);
    }

    public static void main(String[] args) {
        DiseaseServiceImpl diseaseService = new DiseaseServiceImpl();
        diseaseService.selectByPage(new Page<>(12, 312));
    }

    @Override
    @NonNull
    public List<Disease> selectByName(String name, Page<Disease> page) {
        // TODO 适合用倒排索引ES
        return super.lambdaQuery().like(Disease::getName, name).page(page).getRecords();
    }

    @Override
    @NonNull
    public List<Integer> queryIdsByName(String diseaseName) {
        // TODO 适合用倒排索引ES
        return super.lambdaQuery()
                .select(Disease::getId)
                .like(Disease::getName, diseaseName)
                .list()
                .stream()
                .map(Disease::getId)
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public List<String> queryDiseaseNameByIds(List<Integer> diseaseIds) {
        // 用Redis增强
        List<Disease> diseases = selectByIds(diseaseIds);
        return diseases.stream().map(Disease::getName).collect(Collectors.toList());
    }

    @Override
    public void validOnWrite(UserDto user) {
        if (user == null) {
            throw new UnauthorizedException("未登录不能执行");
        }
        String identityCardId = user.getIdentityCardId();
        if (identityCardId == null) {
            throw new UnauthorizedException("未实名不能执行");
        }
        Role role = user.getRole();
        switch (role) {
            case UNKNOWN:
            case PATIENT:
            case NORMAL_DOCTOR:
            case MEDICATION_DOCTOR:
                throw new UnauthorizedException("权限不足");
            case CHARGE_DOCTOR:
            case DEVELOPER:
            case DATABASE_ADMINISTRATOR:
                break;
            default:
                throw new ServerException("Unexpected role value: " + role);
        }
    }

    @Override
    @NonNull
    public Integer register(Disease disease) {
        disease.setId(null);
        boolean saved = super.save(disease);
        if (saved) {
            log.debug("成功保存疾病");
        } else {
            throw new BadRequestException("保存疾病失败");
        }
        return disease.getId();
    }
}