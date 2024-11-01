package pers.apong.lefriend.controller;

import cn.hutool.core.bean.BeanUtil;
import pers.apong.lefriend.common.BaseResponse;
import pers.apong.lefriend.common.DeleteRequest;
import pers.apong.lefriend.common.ErrorCode;
import pers.apong.lefriend.common.ResultUtils;
import pers.apong.lefriend.excpetion.BusinessException;
import pers.apong.lefriend.model.domain.Tag;
import pers.apong.lefriend.model.domain.User;
import pers.apong.lefriend.model.request.tag.TagAddRequest;
import pers.apong.lefriend.model.request.tag.TagUpdateRequest;
import pers.apong.lefriend.service.TagService;
import pers.apong.lefriend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 队伍接口
 * @author apong
 */
@RestController
@RequestMapping("/tag")
@Slf4j
public class TagController {
    @Resource
    private TagService tagService;
    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addTag(@RequestBody TagAddRequest tagAddRequest) {
        if (tagAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Tag tag = BeanUtil.copyProperties(tagAddRequest, Tag.class);
        User loginUser = userService.getLoginUser();
        tag.setUserId(loginUser.getId());
        boolean b = tagService.save(tag);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        return ResultUtils.success(tag.getId());
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTag(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = tagService.removeById(deleteRequest.getId());
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTag(@RequestBody TagUpdateRequest tagUpdateRequest) {
        if (tagUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Tag tag = BeanUtil.copyProperties(tagUpdateRequest, Tag.class);
        boolean b = tagService.updateById(tag);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 获取所有标签
     *
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<Tag>> listTags() {
        List<Tag> tagList = tagService.list();
        return ResultUtils.success(tagList);
    }
}
