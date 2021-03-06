package tech.greatinfo.sellplus.controller.merchant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import tech.greatinfo.sellplus.config.converter.StringToDateConverter;
import tech.greatinfo.sellplus.domain.Merchant;
import tech.greatinfo.sellplus.domain.Seller;
import tech.greatinfo.sellplus.domain.coupons.Coupon;
import tech.greatinfo.sellplus.domain.coupons.CouponsObj;
import tech.greatinfo.sellplus.service.*;
import tech.greatinfo.sellplus.utils.obj.ResJson;

import javax.transaction.Transactional;

/**
 * Created by Ericwyn on 18-9-6.
 */
@RestController
public class CouponsController {
    private static final Logger logger = LoggerFactory.getLogger(CouponsController.class);

    @Autowired
    CouponsService modelService;

    @Autowired
    CouponsObjService objService;

    @Autowired
    TokenService tokenService;
    // 获取全部优惠卷模板信息

    @Autowired
    SellerSerivce sellerSerivce;

    @Autowired
    CouponsHistoryService couponsHistoryService;

    @InitBinder
    public void intDate(WebDataBinder dataBinder){
        dataBinder.addCustomFormatter(new StringToDateConverter("yyyy-MM-dd hh:mm:ss"));
//        dataBinder.addCustomFormatter(new StringToBooleanConverter());
    }

    /**
     * 新增优惠卷模板
     *
     * POST
     *      token
     *      content     str     卷的描述
     *      finite      bool    是否高级别的无线优惠卷
     *      num         int     优惠卷的数量（只有 finite 为 true 的时候，这个参数才是有效的）
     *      startDate   date    yyyy-MM-dd hh:mm:ss 有效期开始时间
     *      endDate     date    格式同上，有效期的结束时间
     *
     * @param token
     * @param coupons
     * @return
     */
    @RequestMapping(value = "/api/mer/addCouponModel",method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    @Transactional
    public ResJson addProduct(@RequestParam(name = "token") String token,
//                              @RequestParam(name = "content") String content,
//                              @RequestParam(name = "finite") String finite,
//                              @RequestParam(name = "num") Integer num,
//                              @RequestParam(name = "startDate") Date startDate,
//                              @RequestParam(name = "startDate") Date startDate,

                              //@ModelAttribute Coupon coupons
                                Coupon coupons){
        try {
            Merchant merchant;
            if ((merchant = (Merchant) tokenService.getUserByToken(token)) != null){
                modelService.save(coupons);
//                if (coupons.getFinite()){
//                    int num = coupons.getNum();
//                    List<CouponsObj> list = new ArrayList<>(num);
//                    for (int i=0;i<num;i++){
//                        // 新建 num 个尚未发出去的卷
//                        CouponsObj obj = new CouponsObj();
//                        obj.setCode(objService.getRandomCouponCode());
//                        obj.setCoupon(coupons);
//                        obj.setExpired(false);
//                        objService.save(obj);
//                    }
//                    objService.save(list);
//                }
                return ResJson.successJson("add coupons success", coupons);
            }else {
                return ResJson.errorAccessToken();
            }
        }catch (Exception e){
            logger.error("/api/mer/addCouponModel -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    /**
     * 获取全部的优惠卷模板
     *
     * TODO 改为分页查询
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/api/mer/getCouponModel",method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    public ResJson getCouponModel(@RequestParam(name = "token") String token,
                              @RequestParam(name = "start",defaultValue = "0") Integer start,
                              @RequestParam(name = "num",defaultValue = "999") Integer num){
        try {
            Merchant merchant;
            if ((merchant = (Merchant) tokenService.getUserByToken(token)) != null){
                Page<Coupon> all = modelService.findAll(start, num);
                return ResJson.successJson("get all coupons model success",all);
            }else {
                return ResJson.errorAccessToken();
            }
        }catch (Exception e){
            logger.error("/api/mer/getCouponModel -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    /**
     * 获取全部优惠卷实体
     * POST
     *      token
     *      start
     *      end
     *
     * @param token
     * @param start
     * @param num
     * @return
     */
    @RequestMapping(value = "/api/mer/getCouponObj",method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    public ResJson getCouponObj(@RequestParam(name = "token") String token,
                              @RequestParam(name = "start",defaultValue = "0") Integer start,
                              @RequestParam(name = "num",defaultValue = "999") Integer num){
        try {
            Merchant merchant;
            if ((merchant = (Merchant) tokenService.getUserByToken(token)) != null){
                Page<CouponsObj> all = objService.findAll(start, num);
                return ResJson.successJson("get all coupons object success",all);
            }else {
                return ResJson.errorAccessToken();
            }
        }catch (Exception e){
            logger.error("/api/mer/getCouponObj -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    /**
     *
     * 删除某个优惠卷模板
     * POST
     *      token
     *      cid     str     优惠卷模板 id
     *
     * @param token
     * @param cid
     * @return
     */
    @RequestMapping(value = "/api/mer/delCouponModel",method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    public ResJson getCouponObj(@RequestParam(name = "token") String token,
                                @RequestParam(name = "cid") Long cid){
        try {
            Merchant merchant;
            if ((merchant = (Merchant) tokenService.getUserByToken(token)) != null){
                Coupon coupon = modelService.findOne(cid);
                if (coupon != null){
                    modelService.delete(coupon);
                    return ResJson.successJson("delete success");
                }else {
                    return ResJson.failJson(-1,"coupon id error",null);
                }
            }else {
                return ResJson.errorAccessToken();
            }
        }catch (Exception e){
            logger.error("/api/mer/delCouponModel -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    /**
     * 更新券模板
     * @param token
     * @param coupon
     * @return
     */
    @RequestMapping(value = "/api/mer/updateCouponModel",method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    public ResJson updateCouponObj(@RequestParam(name = "token") String token,
                                  @ModelAttribute Coupon coupon){
        try {
            if (tokenService.getUserByToken(token) != null){
                if (coupon.getId() == null){
                    return ResJson.failJson(7004,"couponsObj id error",null);
                }
                Coupon oldCoupon;
                if ((oldCoupon = modelService.findOne(coupon.getId())) == null ){
                    return ResJson.failJson(7003,"无法更新, 权限错误",null);
                }
                modelService.updateCoupon(oldCoupon,coupon);
                return ResJson.successJson("update Coupon success");
            }else {
                return ResJson.errorAccessToken();
            }
        }catch (Exception e){
            logger.error("/api/mer/updateCouponModel -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }


    /**
     *
     * 商家端后台端核销优惠卷
     *
     * POST
     *      token   电脑端公司管理员的登录 token
     *      code    优惠卷的 code
     *
     * @param token
     * @param couponCode
     * @return
     */
    @RequestMapping(value = "/api/mer/writeOffCoupons", method = RequestMethod.POST)
    public ResJson getCustomerNews(@RequestParam("token") String token,
                                   @RequestParam(value = "code") String couponCode) {
        try {
            Merchant merchat;
            if ((merchat = (Merchant) tokenService.getUserByToken(token)) != null) {
                CouponsObj coupon = objService.findByCode(couponCode);
                if (coupon == null){
                    return ResJson.failJson(-1,"优惠卷代码错误",null);
                }else {
                    // 核销优惠卷
                    Seller seller = sellerSerivce.getDefaultSeller();
                    if (seller == null){
                        return ResJson.failJson(-1,"尚未设置默认销售信息，请联系开发人员",null);
                    }
                    objService.writeOffCoupons(coupon,seller);
                    return  ResJson.successJson("write off coupon success");
                }
            } else {
                return ResJson.errorAccessToken();
            }
        } catch (Exception e) {
            logger.error("/api/sell/writeOffCoupons -> ", e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }


    /**
     * 销售后台获取所有的核销记录
     *
     * POST
     *      token 销售登录后获取的 token
     *      start 分页开始，默认为 0
     *      num   分页一个页面含有的数据量，默认为 999
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/api/mer/writeOffHistory", method = RequestMethod.POST)
    public ResJson writeOffHistory(@RequestParam("token") String token,
                                   @RequestParam(value = "start", defaultValue = "0") Integer start,
                                   @RequestParam(value = "num", defaultValue = "9999") Integer num) {
        try {
            Merchant merchat;
            if ((merchat = (Merchant) tokenService.getUserByToken(token)) != null) {
                return ResJson.successJson("get all write off history success", couponsHistoryService.findAll(start, num));
            }else {
                return ResJson.errorAccessToken();
            }
        } catch (Exception e) {
            logger.error("/api/sell/writeOffCoupons -> ", e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }
}
