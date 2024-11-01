package pers.apong.lefriend.model.request.tag;

import lombok.Data;

/**
 * 新增请求
 */
@Data
public class TagAddRequest {
    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 父标签 id
     */
    private Long parentId;

    /**
     * 0 - 不是，1 - 父标签
     */
    private Integer isParent;
}
