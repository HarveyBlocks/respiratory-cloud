package org.harvey.respiratory.cloud.patient.server.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.harvey.respiratory.cloud.common.pojo.entity.Patient;

/**
 * 患者
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-09 20:28
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
    /*
     * <pre>{@code
     *
     *     <select id="queryByRegisterUser" resultMap="PatientDtoMap">
     *         select
     *         p.id,
     *         p.phone,
     *         p.name,
     *         p.sex,
     *         p.birth_date,
     *         p.address,
     *         p.height,
     *         p.weight,
     *         p.identity_card_id,
     *         h.type as healthcare_type,
     *         h.healthcare_code,
     *         h.balance as healthcare_balance
     *         from (
     *         select patient_id
     *         from tb_user_patient_intermediation
     *         <where>
     *             tb_user_patient_intermediation.user_id = #{userId}
     *         </where>
     *         limit ${start},${len}
     *         ) AS i
     *         INNER JOIN tb_patient p
     *         on i.patient_id = p.id
     *         LEFT JOIN tb_healthcare h
     *         on h.healthcare_id = p.healthcare_id
     *         ;
     *     </select>
     *
     * }</pre>
     */

}
