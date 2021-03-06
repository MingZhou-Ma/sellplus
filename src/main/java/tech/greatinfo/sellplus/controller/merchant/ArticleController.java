package tech.greatinfo.sellplus.controller.merchant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import tech.greatinfo.sellplus.config.converter.StringToDateConverter;
import tech.greatinfo.sellplus.domain.article.Article;
import tech.greatinfo.sellplus.service.ArticleService;
import tech.greatinfo.sellplus.service.MerchantService;
import tech.greatinfo.sellplus.service.TokenService;
import tech.greatinfo.sellplus.utils.obj.ResJson;

/**
 * Created by Ericwyn on 18-8-9.
 */
@RestController
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    ArticleService articleService;

    @Autowired
    TokenService tokenService;

    @Autowired
    MerchantService merchantService;

    // 文章获取接口在 PublicResController

    @InitBinder
    public void intDate(WebDataBinder dataBinder){
        dataBinder.addCustomFormatter(new StringToDateConverter("yyyy-MM-dd hh:mm:ss"));
//        dataBinder.addCustomFormatter(new StringToBooleanConverter());
    }


    /**
     * 增加文章
     *
     * POST
     *      token   后台商家用户登录后的 token
     *      title   标题
     *      content 文章内容
     *      author  作者
     *      createDate  创建时间 (yyyy-MM-dd hh:mm:ss)
     *      intro   文章简介
     *
     * @param token
     * @param article
     * @return
     */
    @RequestMapping(value = "/api/mer/addArticle",method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    public ResJson addArticle(@RequestParam(name = "token") String token,
                               @ModelAttribute Article article){
        try {
            if (tokenService.getUserByToken(token) != null){
                return ResJson.successJson("add Article Success", articleService.save(article));
            }else {
                return ResJson.errorAccessToken();
            }
        }catch (Exception e){
            logger.error("/api/mer/addArticle -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    //

    /***
     * 删除文章
     *
     * POST
     *      token
     *      articleid 文章 id
     *
     * @param token
     * @param articleId
     * @return
     */
    @RequestMapping(value = "/api/mer/delArticle",method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    public ResJson addActivity(@RequestParam(name = "token") String token,
                               @RequestParam(name = "articleid") Long articleId){
        try {
            if (tokenService.getUserByToken(token) != null){
                articleService.delete(articleId);
                return ResJson.successJson("delete success");
            }else {
                return ResJson.errorAccessToken();
            }
        }catch (Exception e){
            logger.error("/api/mer/addArticle -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    // 修改文章
    @RequestMapping(value = "/api/mer/updateArticle",method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    public ResJson updateActivity(@RequestParam(name = "token") String token,
                                  @ModelAttribute Article article ){
        try {
            if (tokenService.getUserByToken(token) != null){
                if (article.getId() == null){
                    return ResJson.failJson(7004,"article id error",null);
                }
                Article oldArticle;
                if ((oldArticle = articleService.findOne(article.getId())) == null ){
                    return ResJson.failJson(7003,"无法更新, 权限错误",null);
                }
                articleService.updateArticle(oldArticle,article);
                return ResJson.successJson("update Article success");
            }else {
                return ResJson.errorAccessToken();
            }
        }catch (Exception e){
            logger.error("/api/mer/updateActivity -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

}
