package org.harvey.respiratory.cloud.disease.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.harvey.respiratory.cloud.common.pojo.entity.Disease;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class DiseaseServiceImplTest {

    @Resource
    private DiseaseServiceImpl diseaseService;

    @Test
    void selectByIds() {

    }

    @Test
    void selectByPage() {
        List<Disease> diseases = diseaseService.selectByPage(Page.of(2, 70));
        System.out.println(diseases);
    }
}