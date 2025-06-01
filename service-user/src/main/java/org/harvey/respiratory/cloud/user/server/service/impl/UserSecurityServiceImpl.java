package org.harvey.respiratory.cloud.user.server.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.api.service.UserSecurityService;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.harvey.respiratory.cloud.common.constants.ServerConstants;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.DaoException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.LoginFormDto;
import org.harvey.respiratory.cloud.common.pojo.dto.RegisterFormDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserSecurityDto;
import org.harvey.respiratory.cloud.common.pojo.entity.UserSecurity;
import org.harvey.respiratory.cloud.common.security.JwtProperties;
import org.harvey.respiratory.cloud.common.security.JwtTool;
import org.harvey.respiratory.cloud.common.utils.JacksonUtil;
import org.harvey.respiratory.cloud.common.utils.RandomUtil;
import org.harvey.respiratory.cloud.common.utils.RegexUtils;
import org.harvey.respiratory.cloud.common.utils.UserDtoRedissonLock;
import org.harvey.respiratory.cloud.common.utils.identifier.IdentifierIdPredicate;
import org.harvey.respiratory.cloud.user.server.dao.UserSecurityMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 14:10
 */
@Slf4j
@Service
@org.apache.dubbo.config.annotation.DubboService
public class UserSecurityServiceImpl extends ServiceImpl<UserSecurityMapper, UserSecurity> implements
        UserSecurityService {
    private final UserDtoRedissonLock userDtoRedissonLock;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtTool jwtTool;
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IdentifierIdPredicate identifierIdPredicate;
    @Resource
    private RandomUtil randomUtil;
    @Resource
    private JacksonUtil jacksonUtil;


    public UserSecurityServiceImpl(UserDtoRedissonLock redissonLock) {
        this.userDtoRedissonLock = redissonLock;
    }

    @NonNull
    private static Map<String, String> user2Map(UserDto user) {
        return Map.of(ID_FIELD, user.getId().toString(), NAME_FIELD, user.getName(), "identityCardId",
                String.valueOf(user.getIdentityCardId()), TIME_FIELD, ServerConstants.RESTRICT_REQUEST_TIMES
        );
    }

    @Override
    @NonNull
    public String sendCode(String phone) {
        String code;
        log.debug("phone = `{}`", phone);
        if (!RegexUtils.isPhoneEffective(phone)) {
            throw new BadRequestException("手机号不合法");
        }
        code = String.format("%06d", randomUtil.uniform(0, 1000000));
        // 发送短信验证码
        // 搞个假的
        log.debug("\n尊敬的" + phone + "用户:\n\t您的短信验证码是: " + code);
        return code;
    }

    @Override
    @NonNull
    public UserSecurityDto loginByCode(String codeCache, String phone, String code) {
        // code的长度已经正确,code不为null
        if (!code.equals(codeCache)) {
            throw new BadRequestException("验证码不正确");
        }

        // 如果验证码手机号一致, 去数据库查找用户
        try {
            return selectByPhone(phone);
        } catch (ResourceNotFountException e) {// 不存在就创建新用户并保存
            UserSecurityDto user = new UserSecurityDto();
            user.setPhone(phone);
            //newUser.setId()主键会自增, 不必管他
            // user.setIcon(UserSecurity.DEFAULT_ICON);//头像使用默认的
            user.setName(UserSecurity.DEFAULT_NAME);//昵称使用默认的
            // 随机生成或直接为null,为null就百分百无法通过密码登录了.
            // 随机可能被猜中?
            user.setUpdateTime(LocalDateTime.now());
            //这里就先不要增改扰了人家数据库清静
            baseMapper.insert(new UserSecurity(user));
            // user为null, user的id怎么确认? 再查一次? 太反人类了吧
            user = selectByPhone(phone);
            // log.debug(String.valueOf(user));
            // 返回user
            return user;

        }
    }

    @Override
    @NonNull
    public UserSecurityDto loginByPassword(String phone, String password) {
        if (!RegexUtils.isPasswordEffective(password)) {
            throw new BadRequestException("密码格式不正确,应是4~32位的字母、数字、下划线");
        }
        // 依据电话号码从service取数据
        UserSecurity user;
        try {
            user = selectByPhoneSimply(phone);
        } catch (ResourceNotFountException e) {
            UserSecurityDto nullUser = new UserSecurityDto();
            nullUser.setId(-1L);
            return nullUser;//用户名不存在
        }
        // 取出来的数据和密码作比较
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // password经过检验, 非null, 数据库里的password可能是null
            throw new BadRequestException("用户名或密码错误");
        }
        // log.debug(String.valueOf(user));
        // 正确则返回user值
        return user.buildDto();
    }

    @Override
    @NonNull
    public String chooseLoginWay(LoginFormDto loginForm) {
        UserSecurityDto user /* = null*/;
        String phone = loginForm.getPhone();
        String code = loginForm.getCode();
        String password = loginForm.getPassword();
        if (!RegexUtils.isPhoneEffective(phone)
            // 网上说参数校验放在controller, 这算参数校验吗?
        ) {
            throw new UnauthorizedException("请正确输入电话号");
        }
        if ((password == null || password.isEmpty()) == (code == null || code.isEmpty())) {
            // 无法决定是密码登录还是验证码登录的情况
            throw new BadRequestException("请正确输入验证码或密码");
        }
        if (code != null && !code.isEmpty()) {
            if (code.length() != 6) {
                throw new BadRequestException("请输入正确格式的验证码");
            }
            // 使用验证码登录
            String codeCache = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
            if (codeCache == null || codeCache.isEmpty()) {
                throw new BadRequestException("该手机未申请验证码, 请确认手机号或是否已经请求验证码");
            }
            user = this.loginByCode(codeCache, phone, code);
            // 如果成功了, 就删除Redis缓存
            stringRedisTemplate.delete(RedisConstants.LOGIN_CODE_KEY + phone);
            // 否则不删除会话,给用户一个再次输入验证码的机会
        } else /*if(password!=null&&!password.isEmpty())*/ {
            user = this.loginByPassword(phone, password);
            if (user.getId().equals(-1L)) {
                throw new UnauthorizedException("该用户不存在");
            }
        }


        // session.setAttribute(ServerConstants.USER_SESSION_KEY,new UserDto(user.getId(),user.getNickName(),user.getIcon()));
        // 将用户DTO存入Redis
        String token =// 生成随机Token,hutool工具包
                jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());//true表示不带中划线;


        saveToRedis(user.buildSimple(), token);
        // 返回token
        return token;
    }

    @Override
    @Transactional
    @NonNull
    public String register(RegisterFormDto registerForm) {
        UserSecurity registerUser = new UserSecurity();
        String phone = registerForm.getPhone();
        registerUser.setPhone(phone);
        registerUser.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        registerUser.setName(registerForm.getName());
        boolean saved = save(registerUser);
        if (!saved) {
            log.error("保存失败错误");
            throw new DaoException(DaoException.Operation.SAVE_FAIL, "保存失败");
        }
        // 依据电话号码从service取数据
        UserSecurityDto user;
        try {
            user = selectByPhone(registerUser.getPhone());
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("不能通过电话 " + registerUser.getPhone() + " 找到用户");
        }
        // 取出来的数据和密码作比较

        // 将用户DTO存入Redis
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        saveToRedis(user.buildSimple(), token);
        return token;
    }

    @Override
    @Transactional
    public void updateUser(Long currentUserId, UserDto newUser, String token) {
        // 更新实体数据
        UserSecurity user = super.getById(currentUserId);
        if (user == null) {
            throw new ResourceNotFountException("can not found: " + currentUserId);
        }
        String name = newUser.getName();
        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }


        String identityCardId = newUser.getIdentityCardId();
        if (identityCardId != null && identifierIdPredicate.test(identityCardId)) {
            // 正确的身份证
            // 更新身份证
            // 算作实名了
            user.setIdentityCardId(identityCardId);
            log.debug("user_security表不再存储role权限, 这里至少简单的实名");
        } else {
            log.debug("无效的身份证, 实名失败, 如果之前有实名, 保留之前的权限, 否则,权限不变 ");
        }

        user.setUpdateTime(LocalDateTime.now());
        // 更新

        String tokenKey = RedisConstants.QUERY_USER_KEY + jwtTool.parseToken(token);
        stringRedisTemplate.delete(tokenKey);
        // 更新数据库
        UserSecurityService userService = (UserSecurityService) AopContext.currentProxy();
        userService.updateUserSecurity(user.buildDto());
    }

    private void saveToRedis(UserDto userDTO, String token) {
        if (userDTO == null) {
            throw new BadRequestException("用户信息为null");
        }
        String tokenKey = RedisConstants.QUERY_USER_KEY + jwtTool.parseToken(token);
        saveUser2Redis(tokenKey, user2Map(userDTO), RedisConstants.QUERY_USER_TTL);
    }

    @Override
    @NonNull
    public UserSecurityDto selectByPhone(String phone) {
        return selectByPhoneSimply(phone).buildDto();
    }

    private UserSecurity selectByPhoneSimply(String phone) {
        UserSecurity userSecurity = super.lambdaQuery().eq(UserSecurity::getPhone, phone).one();
        if (userSecurity == null) {
            throw new ResourceNotFountException("can not find by phone: " + phone);
        }
        return userSecurity;
    }

    @Override
    @NonNull
    public UserSecurityDto selectByIdentityCardId(String identityCardId) {
        LambdaQueryWrapper<UserSecurity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select().eq(UserSecurity::getIdentityCardId, identityCardId);
        UserSecurity userSecurity = baseMapper.selectOne(lambdaQueryWrapper);
        if (userSecurity == null) {
            throw new ResourceNotFountException("can not find by identityCardId: " + identityCardId);
        }
        return userSecurity.buildDto();
    }

    @Override
    public void updateUserSecurity(UserSecurityDto newSecurity) {
        boolean updated = super.updateById(new UserSecurity(newSecurity));
        if (!updated) {
            throw new BadRequestException("更新数据失败, 未找到用户");
        }
    }

    @Override
    @Nullable
    public UserDto queryUserByIdWithRedisson(Long userId, Long currentUserId) throws InterruptedException {
        // Redis里没有数据

        String key = RedisConstants.QUERY_USER_KEY + userId;
        String lockKey = RedisConstants.USER_LOCK_KEY + currentUserId;
        return userDtoRedissonLock.asynchronousLock(
                lockKey, () -> fastQueryUserByIdWithRedisson(key, userId), () -> getFromDbAndWriteToCache(key, userId));
    }


    @Nullable
    private UserDto fastQueryUserByIdWithRedisson(String key, Long userId) {
        log.debug("queryMutexFixByLock");

        // 从缓存查
        log.debug("用户:" + userId + "从缓存查");
        Map<Object, Object> objectMap = stringRedisTemplate.opsForHash().entries(key);
        Map<String, Object> userFieldMap = new HashMap<>();
        objectMap.forEach((k, v) -> userFieldMap.put(String.valueOf(k), v));
        if (userFieldMap.isEmpty()) {
            throw UserDtoRedissonLock.haveToExecuteSlowException();
        } else if (((String) userFieldMap.get("id")).isEmpty()) {
            log.warn("Redis中存在的假数据" + userId);
            return null;
        } else {
            // 在Redis中有正常的数据
            // 第三个参数: 是否忽略转换过程中产生的异常
            userFieldMap.remove(UserSecurityService.TIME_FIELD);
            try {
                return jacksonUtil.toBean(userFieldMap, UserDto.class);
            } catch (Exception e) {
                log.error("在转化UserFieldMap时出现错误错误" + userFieldMap);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @NonNull
    public UserSecurityDto selectById(long userId) {
        UserSecurity userSecurity = super.getById(userId);
        if (userSecurity == null) {
            throw new ResourceNotFountException("不能依据id " + userId + " 查询到用户");
        }
        return userSecurity.buildDto();
    }

    /**
     * 解决穿透专用
     *
     * @param id  id
     * @param key key
     * @return shop
     */
    @Nullable
    private UserDto getFromDbAndWriteToCache(String key, Long id) {
        // 缓存不存在
        // 使用缓存空对象的逻辑
        log.debug("缓存不存在用户" + id + ", 只能在数据库中查");
        UserDto userDTO = null;
        int ttl = RedisConstants.CACHE_NULL_TTL;
        Map<String, String> userFieldMap = Map.of("id", "");
        // 数据库查
        log.debug("从数据库查用户:" + id);
        UserSecurity user = super.getById(id);
        if (user != null) {
            userDTO = user.buildDto().buildSimple();
            // 存在,写入Cache,更改TTL
            log.debug("数据库中存在用户:" + id);
            userFieldMap = user2Map(userDTO);
            ttl = RedisConstants.QUERY_USER_TTL;
        } else {
            // 是虚假的用户,存入Redis,防止虚空索敌
            log.warn(id + "是虚假的用户,Redis中存入假数据中");
        }
        saveUser2Redis(key, userFieldMap, ttl);
        return userDTO;
    }

    private void saveUser2Redis(String key, Map<String, String> map2Redis, int ttl) {
        stringRedisTemplate.opsForHash().putAll(key, map2Redis);
        if (-1 != ttl) {
            stringRedisTemplate.expire(key, plusRandomSec(ttl), TimeUnit.SECONDS);
        }
    }

    /**
     * 通过随机改变ttl, 防止雪崩
     *
     * @return 增加了随机值的ttl
     */
    @NonNull
    private Long plusRandomSec(int ttl) {
        long random;
        if (ttl <= 10L) {
            random = 0;
        } else {
            int exSec = ttl / 10;
            random = randomUtil.uniform(-exSec, exSec);
        }
        LocalDateTime time = LocalDateTime.now().plusSeconds(ttl + random);
        return time.toEpochSecond(ZoneOffset.of("+8"));
    }


}
