package pers.apong.lefriend.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import pers.apong.lefriend.model.domain.Team;
import pers.apong.lefriend.model.vo.TeamUserVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class TeamMapperTest {

    @Resource
    private TeamMapper teamMapper;

    @Test
    void queryTeamUser() {
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        wrapper.between("t.create_time", LocalDateTime.now().minusHours(5L), LocalDateTime.now());
        List<TeamUserVo> teamUserVoList = teamMapper.queryAllTeamWithUser(wrapper, 0, 5);
        Assertions.assertTrue(teamUserVoList.size() > 0);
    }
}
