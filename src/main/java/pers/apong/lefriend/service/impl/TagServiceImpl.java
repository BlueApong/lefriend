package pers.apong.lefriend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.apong.lefriend.common.ErrorCode;
import pers.apong.lefriend.excpetion.BusinessException;
import pers.apong.lefriend.model.domain.Tag;
import pers.apong.lefriend.model.domain.User;
import pers.apong.lefriend.service.TagService;
import pers.apong.lefriend.mapper.TagMapper;
import pers.apong.lefriend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author apong
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

    @Resource
    UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTags(List<String> tagNameList) {
        User loginUser = userService.getLoginUser();
        List<Tag> newTagList = new ArrayList<>();
        for (String tagName : tagNameList) {
            if (!this.validate(tagName)) {
                continue;
            }
            Tag tag = new Tag();
            tag.setTagName(tagName);
            tag.setUserId(loginUser.getId());
            newTagList.add(tag);
        }
        boolean b = this.saveBatch(newTagList);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "自定义标签新增失败");
        }
        return true;
    }

    private boolean validate(String tagName) {
        // 标签是否重复
        List<Tag> tagList = list();
        for (Tag tag : tagList) {
            String aTag = tag.getTagName().toUpperCase();
            if (aTag.equals(tagName.toUpperCase())) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUserTags(List<String> tagNameList, List<String> newTagList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser();
        // 建立新标签
        if (CollUtil.isNotEmpty(newTagList)) {
            this.addTags(newTagList);
        }
        // 新增用户标签
        User oldUser = userService.query().select("tags").eq("id", loginUser.getId()).one();
        List<String> userTagList = JSONUtil.toList(oldUser.getTags(), String.class);
        boolean b = userTagList.addAll(tagNameList);
        boolean update = userService.update()
                .set("tags", JSONUtil.toJsonStr(userTagList))
                .eq("id", loginUser.getId())
                .update();
        if (!b || !update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户标签添加失败");
        }
        return true;
    }
}




