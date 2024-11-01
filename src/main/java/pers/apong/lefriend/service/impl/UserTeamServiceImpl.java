package pers.apong.lefriend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.apong.lefriend.model.domain.UserTeam;
import pers.apong.lefriend.service.UserTeamService;
import pers.apong.lefriend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author a
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-05-07 22:09:14
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




