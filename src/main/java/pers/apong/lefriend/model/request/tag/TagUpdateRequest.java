package pers.apong.lefriend.model.request.tag;

import lombok.Data;

import java.io.Serializable;

/**
 * 标签更新请求
 */
@Data
public class TagUpdateRequest implements Serializable {

    private static final long serialVersionUID = 2542066413652521609L;

    /**
     * id
     */
    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 0 - 不是，1 - 父标签
     */
    private Integer isParent;
}
