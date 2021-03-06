package tech.greatinfo.sellplus.service;

import com.alibaba.fastjson.JSONObject;
import tech.greatinfo.sellplus.domain.Poster;
import tech.greatinfo.sellplus.utils.obj.ResJson;

/**
 * 描述：
 *
 * @author Badguy
 */
public interface PosterService {

    ResJson addPoster(String token, Poster poster);

    ResJson queryPosterList(String token, Integer type, Integer isPoster, Integer start, Integer num);

    ResJson updatePoster(String token, Poster poster);

    ResJson deletePoster(String token, Long posterId);

    ResJson findPosterList(JSONObject jsonObject);
}
