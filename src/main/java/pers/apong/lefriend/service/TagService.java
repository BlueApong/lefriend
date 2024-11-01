package pers.apong.lefriend.service;

import pers.apong.lefriend.model.domain.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author a
*/
public interface TagService extends IService<Tag> {

    /**
     * 批量添加标签
     *
     * @param tagNameList
     * @return
     */
    boolean addTags(List<String> tagNameList);

    /**
     * 给用户添加标签，并建立新标签
     *
     * @param tagNameList
     * @param newTagList
     * @return
     */
    boolean addUserTags(List<String> tagNameList, List<String> newTagList);
}
