package pers.apong.lefriend.model.request.user;

import lombok.Data;

import java.util.List;

@Data
public class UserTagAddRequest {
    /**
     * 用户新增标签
     */
    private List<String> tagNameList;

    /**
     * 新建标签
     */
    private List<String> newTagList;
}
