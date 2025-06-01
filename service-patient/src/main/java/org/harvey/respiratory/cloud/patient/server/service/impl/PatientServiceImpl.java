package org.harvey.respiratory.cloud.patient.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.HealthcareService;
import org.harvey.respiratory.cloud.api.service.PatientService;
import org.harvey.respiratory.cloud.api.service.UserPatientIntermediationService;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.PatientDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.Healthcare;
import org.harvey.respiratory.cloud.common.pojo.entity.Patient;
import org.harvey.respiratory.cloud.common.pojo.entity.UserPatientIntermediation;
import org.harvey.respiratory.cloud.common.pojo.enums.Role;
import org.harvey.respiratory.cloud.common.utils.RegexUtils;
import org.harvey.respiratory.cloud.common.utils.identifier.AddressOnIdLoader;
import org.harvey.respiratory.cloud.common.utils.identifier.IdentifierCardId;
import org.harvey.respiratory.cloud.common.utils.identifier.IdentifierIdPredicate;
import org.harvey.respiratory.cloud.patient.server.dao.PatientMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:38
 * @see Patient
 * @see PatientMapper
 * @see PatientService
 */
@org.apache.dubbo.config.annotation.DubboService
@Service
@Slf4j
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    @DubboReference
    private UserPatientIntermediationService userPatientIntermediationService;
    @DubboReference
    private HealthcareService healthcareService;
    @Resource
    private IdentifierIdPredicate identifierIdPredicate;
    @Resource
    private AddressOnIdLoader addressOnIdLoader;

    private static PatientService currentProxy() {
        return (PatientService) AopContext.currentProxy();
    }

    @Override
    public long registerPatientInformation(PatientDto patientDto, long currentUserId) {
        // 先查询确认不存在
        String phone = patientDto.getPhone();
        if (!RegexUtils.isPhoneEffective(phone)) {
            throw new BadRequestException("错误的电话号码格式");
        }
        String identityCardId = patientDto.getIdentityCardId();
        if (!identifierIdPredicate.test(identityCardId)) {
            throw new BadRequestException("错误的身份证号格式");
        }

        IdentifierCardId cardId = IdentifierCardId.phase(addressOnIdLoader, identityCardId);
        if (!cardId.getBrithDate().equals(patientDto.getBirthDate())) {
            throw new BadRequestException("身份证号上的生日和提交的生日冲突");
        }
        // 查询是否存在记录
        PatientService proxy = currentProxy();

        try {
            Patient patientFromDb = selectByIdCard(identityCardId);
            // 存在则添加关系, 检查医保
            return proxy.registerForExistPatient(patientFromDb, patientDto.buildHealthcare(), currentUserId);
        } catch (ResourceNotFountException e) {
            // 不存在就保存
            // insert patient
            return proxy.registerForNotExistPatient(
                    patientDto.buildPatient(), patientDto.buildHealthcare(), currentUserId);
        }
    }

    @Override
    @Transactional
    public long registerForExistPatient(
            Patient patientFromDb, Healthcare healthcare, long currentUserId) {
        // 查询是否user-patient建立关系
        Long patientId = patientFromDb.getId();
        if (!userPatientIntermediationService.existRelation(currentUserId, patientId)) {
            // 如果没有建立关系, 需要建立关系
            userPatientIntermediationService.register(currentUserId, patientId);
        }
        // 查询是否有healthcare了;
        if (healthcare == null) {
            // 没有就采用老的医保
            return patientId;
        }
        if (patientFromDb.getHealthcareId() != null) {
            // 已经存在医保
            return patientId;
        }
        // 还没有医保, 就添加医保
        // 保存healthcare, 产生id
        healthcareService.register(healthcare);
        // 病患处的更新医保id
        Long healthcareId = healthcare.getHealthcareId();
        updateHealthcare(healthcareId, patientId);
        return patientId;
    }

    private void updateHealthcare(Long healthcareId, Long patientId) {
        boolean update = super.lambdaUpdate()
                .set(Patient::getHealthcareId, healthcareId)
                .eq(Patient::getId, patientId)
                .update();
        if (update) {
            log.debug("成功添加患者{}的医保号{}", patientId, healthcareId);
        } else {
            log.error("添加患者{}医保号{}失败, 患者可能不存在. ", patientId, healthcareId);
        }
    }

    @Override
    @Transactional
    @NonNull
    public Long registerForNotExistPatient(Patient patient, Healthcare healthcare, long currentUserId) {
        // 保存病患实体
        boolean savedPatient = super.save(patient);
        if (savedPatient) {
            log.debug("成功登记患者记录");
        } else {
            log.error("登记患者失败, 未知原因");
        }
        // insert user-patient中间表
        // 数据库存入之后, 如果存中间表失败了, 还需要
        Long patientId = patient.getId();
        boolean savedIntermediation = userPatientIntermediationService.save(
                new UserPatientIntermediation(currentUserId, patientId));
        if (savedIntermediation) {
            log.debug("登记患者-用户连接成功");
        } else {
            log.error("登记患者-用户连接失败, 未知原因");
        }
        // insert health-care
        if (healthcare == null) {
            return patientId;
        }
        healthcareService.register(healthcare);
        // 病患处的更新医保id
        Long healthcareId = healthcare.getHealthcareId();
        updateHealthcare(healthcareId, patientId);
        return patientId;
    }


    @Override
    @NonNull
    public Patient selectByIdCard(String identityCardId) {
        Patient one = super.lambdaQuery().eq(Patient::getIdentityCardId, identityCardId).one();
        if (one == null) {
            throw new BadRequestException("not null");
        }
        return one;
    }

    @Override
    public void updatePatient(Patient patient, long currentUserId) {
        boolean exist = userPatientIntermediationService.existRelation(currentUserId, patient.getId());
        if (!exist) {
            throw new UnauthorizedException("不能更新无关患者的信息");
        }
        update0(patient, currentUserId);
    }

    private void update0(Patient patient, long currentUserId) {
        // ERROR boolean update = super.update(patient, super.lambdaUpdate().eq(Patient::getId, patient.getId()));
        boolean update = super.update(patient, new LambdaUpdateWrapper<Patient>().eq(Patient::getId, patient.getId()));
        if (update) {
            log.debug("{}用户成功更新{}病患", currentUserId, patient.getId());
        } else {
            log.debug("{}用户更新{}病患失败", currentUserId, patient.getId());
        }
    }

    @Override
    @NonNull
    public List<PatientDto> querySelfPatients(long currentUserId, Page<Patient> page) {
        log.debug("准备查询当前用户的有关患者");
        List<PatientDto> results = queryByRegisterUserPlus(currentUserId, Page.of(page.getCurrent(), page.getSize()));
        log.debug("查询当前用户的有关患者成功!, 有记录: {} 条", results.size());
        return results;
    }


    private List<PatientDto> queryByRegisterUserPlus(long currentUserId, Page<UserPatientIntermediation> page) {
        // TODO TEST
        List<Long> patientIds = userPatientIntermediationService.queryPatientOnUser(currentUserId, page);
        List<Patient> patientList = super.lambdaQuery().in(Patient::getId, patientIds).list();
        List<Long> healthcareIds = patientList.stream().map(Patient::getHealthcareId).collect(Collectors.toList());
        List<Healthcare> healthcareList = healthcareService.queryNullableElementsByIds(healthcareIds);
        int size = patientList.size();
        List<PatientDto> patientDtoList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Patient patient = patientList.get(i);
            Healthcare healthcare = healthcareList.get(i);
            patientDtoList.add(PatientDto.union(patient, healthcare));
        }
        return patientDtoList;
    }

    @Override
    @NonNull
    public PatientDto queryByHealthcare(UserDto user, String healthcareCode) {
        Healthcare healthcare;
        try {
            healthcare = healthcareService.queryByCode(healthcareCode);
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("依据医保号 " + healthcareCode + " 未查询到医保", e);
        }
        Patient patient = queryByHealthcare(healthcare.getHealthcareId());
        validRoleOnQueryAny(user, patient);
        return PatientDto.union(patient, healthcare);
    }

    @Override
    @NonNull
    public PatientDto queryById(UserDto user, long patientId) {
        Patient patient = queryById0(patientId);
        validRoleOnQueryAny(user, patient);
        if (patient.getHealthcareId() == null) {
            return PatientDto.adapt(patient);
        } else {
            Healthcare healthcare = healthcareService.queryById(patient.getHealthcareId());
            return PatientDto.union(patient, healthcare);
        }
    }

    @Override
    @NonNull
    public PatientDto queryByIdentity(long currentUserId, String cardId) {
        if (!identifierIdPredicate.test(cardId)) {
            throw new BadRequestException("不正确的身份证格式");
        }
        Patient patient;
        try {
            patient = selectByIdCard(cardId);
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("依据身份证号" + cardId + "未找到患者", e);
        }
        // 用身份证查询完毕之后, 就绑定了这个用户和这个病患的关系是吧...
        boolean exist = userPatientIntermediationService.existRelation(currentUserId, patient.getId());
        if (!exist) {
            // 注册
            userPatientIntermediationService.register(currentUserId, patient.getId());
        }
        if (patient.getHealthcareId() == null) {
            return PatientDto.adapt(patient);
        }
        Healthcare healthcare = healthcareService.queryById(patient.getHealthcareId());
        return PatientDto.union(patient, healthcare);
    }

    @Override
    public void deletePatientRecord(long patientId, long currentUserId) {
        this.userPatientIntermediationService.delete(patientId, currentUserId);
    }

    @NonNull
    private Patient queryById0(long patientId) {
        Patient patient = super.getById(patientId);
        if (patient == null) {
            throw new ResourceNotFountException("不能依据患者id " + patientId + " 查到患者");
        }
        return patient;
    }

    /**
     * 是否有查询任意病患的权限
     */
    private void validRoleOnQueryAny(UserDto user, Patient patient) {
        Role role = user.getRole();
        switch (role) {
            case UNKNOWN:
                throw new UnauthorizedException("没有获取其他病患的权限");
            case PATIENT:
                Long userId = user.getId();
                Long patientId = patient.getId();
                boolean exist = userPatientIntermediationService.existRelation(userId, patientId);
                if (!exist) {
                    log.warn("用户{}由于权限不能获取患者{}", userId, patientId);
                    throw new UnauthorizedException("没有获取其他病患的权限");
                }
            case NORMAL_DOCTOR:
            case CHARGE_DOCTOR:
            case MEDICATION_DOCTOR:
            case DEVELOPER:
            case DATABASE_ADMINISTRATOR:
                // 直接过
                break;
            default:
                throw new ServerException("Unexpected role value: " + role);
        }
    }

    @NonNull
    private Patient queryByHealthcare(long healthcare) {
        Patient patient = super.lambdaQuery().eq(Patient::getHealthcareId, healthcare).one();
        if (patient == null) {
            throw new BadRequestException("依据医保id{}未找到患者" + healthcare);
        }
        return patient;
    }


    @Override
    @NonNull
    public Patient queryByIdSimply(long patientId) {
        Patient one = super.lambdaQuery().eq(Patient::getId, patientId).one();
        if (one == null) {
            throw new ResourceNotFountException("can not find patient by id: " + patientId);
        }
        return one;
    }

    @Override
    @NonNull
    public Patient queryByCardIdSimply(String identifierCardId) {
        Patient one = super.lambdaQuery().eq(Patient::getIdentityCardId, identifierCardId).one();
        if (one == null) {
            throw new ResourceNotFountException("can not find patient of identifier card id: " + identifierCardId);
        }
        return one;
    }
}