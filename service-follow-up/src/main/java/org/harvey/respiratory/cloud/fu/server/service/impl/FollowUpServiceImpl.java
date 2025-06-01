package org.harvey.respiratory.cloud.fu.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.harvey.respiratory.cloud.api.service.FollowUpService;
import org.harvey.respiratory.cloud.common.pojo.entity.FollowUp;
import org.harvey.respiratory.cloud.fu.server.dao.FollowUpMapper;
import org.springframework.stereotype.Service;


/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-15 12:36
 * @see FollowUp
 * @see FollowUpMapper
 * @see FollowUpService
 */
@Service
@org.apache.dubbo.config.annotation.DubboService
public class FollowUpServiceImpl extends ServiceImpl<FollowUpMapper, FollowUp> implements FollowUpService {

}