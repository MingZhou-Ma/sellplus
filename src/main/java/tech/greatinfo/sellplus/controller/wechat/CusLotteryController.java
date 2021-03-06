package tech.greatinfo.sellplus.controller.wechat;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tech.greatinfo.sellplus.domain.Customer;
import tech.greatinfo.sellplus.domain.Lottery;
import tech.greatinfo.sellplus.domain.LotteryShare;
import tech.greatinfo.sellplus.domain.coupons.Coupon;
import tech.greatinfo.sellplus.domain.coupons.CouponsObj;
import tech.greatinfo.sellplus.repository.LotteryShareRepository;
import tech.greatinfo.sellplus.service.*;
import tech.greatinfo.sellplus.utils.LotteryUtil;
import tech.greatinfo.sellplus.utils.ParamUtils;
import tech.greatinfo.sellplus.utils.exception.JsonParseException;
import tech.greatinfo.sellplus.utils.obj.AccessToken;
import tech.greatinfo.sellplus.utils.obj.ResJson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 抽奖管理
 */
@RestController
public class CusLotteryController {

    private static final Logger logger = LoggerFactory.getLogger(CusLotteryController.class);

    @Autowired
    CompanyService companyService;

    @Autowired
    TokenService tokenService;

    @Autowired
    CouponsService couponsService;

    @Autowired
    CouponsObjService couponsObjService;

    @Autowired
    CustomService customService;

    @Autowired
    LotteryShareRepository lotteryShareRepository;

    /**
     * 抽奖
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/api/cus/lottery", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ResJson lottery(@RequestBody JSONObject jsonObject) {
        try {
            String token = (String) ParamUtils.getFromJson(jsonObject, "token", String.class);
            Customer customer = (Customer) tokenService.getUserByToken(token);
            if (null == customer) {
                return ResJson.errorAccessToken();
            }
            if (customer.getLotteryNum() <= 0) {
                return ResJson.failJson(4000, "无抽奖机会", null);
            }

            String lotteryStr = companyService.getLotteryStr();
            if (StringUtils.isEmpty(lotteryStr)) {
                return ResJson.failJson(5000, "未设置奖品、概率", null);
            }
            String[] lotteryList = lotteryStr.split(",");
            List<Lottery> list = new ArrayList<>();
            for (String lotteryItem : lotteryList) {
                if (StringUtils.isNotEmpty(lotteryItem)) {
                    if (lotteryItem.equals("//")) {
                        lotteryItem = "//0.0";
                        System.out.println(lotteryItem);
                    }
                    String[] item = lotteryItem.split("/");
                    if (StringUtils.isNotEmpty(item[2])) {
                        Lottery lottery;
                        if (StringUtils.isNotEmpty(item[1])) {
                            lottery = new Lottery(item[0], item[1], item[2]);
                        } else {
                            lottery = new Lottery(item[0], "", item[2]);
                        }
                        list.add(lottery);
                    }
                }
            }
            int index = LotteryUtil.drawGift(list); // 抽到的奖品下标
            System.out.println("下标：" + index);
            Lottery lottery = list.get(index);
            System.out.println(lottery);
            if (null != lottery) {
                String couponsId = lottery.getCouponsId();
                if (StringUtils.isNotEmpty(couponsId)) {
                    Coupon coupon = couponsService.findOne(Long.valueOf(couponsId));
                    if (null != coupon) {
                        // 发券
                        CouponsObj couponsObj = new CouponsObj();
                        couponsObj.setCoupon(coupon);
                        couponsObj.setCode(couponsObjService.getRandomCouponCode());
                        couponsObj.setOwn(customer);
                        couponsObj.setNote("抽奖发放的优惠卷");
                        couponsObj.setGeneralTime(new Date());
                        couponsObj.setExpired(false);
                        couponsObjService.save(couponsObj);
                    }
                }
            }
            // 抽奖次数减一
            if (customer.getLotteryNum() > 0) {
                customer.setHasLotteryNum(customer.getHasLotteryNum() + 1);
                customer.setLotteryNum(customer.getLotteryNum() - 1);
                customService.save(customer);

                //更新redis缓存
                AccessToken accessToken = tokenService.getToken(token);
                accessToken.setUser(customer);
                tokenService.saveToken(accessToken);
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("index", index);
            map.put("lottery", lottery);
            return ResJson.successJson("success", map);
        } catch (JsonParseException jse) {
            logger.info(jse.getMessage() + " -> /api/cus/lottery");
            return ResJson.errorRequestParam(jse.getMessage() + " -> /api/cus/lottery");
        } catch (Exception e) {
            logger.error("/api/cus/lottery", e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    /**
     * 抽奖分享
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/api/cus/lottery/share", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ResJson shareLottery(@RequestBody JSONObject jsonObject) {
        try {
            String token = (String) ParamUtils.getFromJson(jsonObject, "token", String.class);
            String uid = (String) ParamUtils.getFromJson(jsonObject, "uid", String.class);
            Customer customer = (Customer) tokenService.getUserByToken(token);
            if (null == customer) {
                return ResJson.errorAccessToken();
            }
            Customer shareCustomer = customService.getByUid(uid);
            if (null == shareCustomer) {
                return ResJson.failJson(4000, "not uid", null);
            }
            if (!customer.getOpenid().equals(shareCustomer.getOpenid())) {

                // 判断是否重复领券
                LotteryShare share = lotteryShareRepository.findByOriginAndOwn(shareCustomer, customer);
                if (null == share) {
                    if (customer.getLotteryNum() + customer.getHasLotteryNum() < 8) {
                        customer.setLotteryNum(customer.getLotteryNum() + 1);
                        customService.save(customer);
                        AccessToken accessToken = tokenService.getToken(token);
                        if (null != accessToken) {
                            accessToken.setUser(customer);
                            tokenService.saveToken(accessToken);
                        }
                    }

                    if (shareCustomer.getLotteryNum() + shareCustomer.getHasLotteryNum() < 8) {
                        shareCustomer.setLotteryNum(shareCustomer.getLotteryNum() + 1);
                        customService.save(shareCustomer);
                        AccessToken accessToken = tokenService.getTokenByCustomOpenId(shareCustomer.getOpenid());
                        if (null != accessToken) {
                            accessToken.setUser(shareCustomer);
                            tokenService.saveToken(accessToken);
                        }
                    }
                    LotteryShare lotteryShare = new LotteryShare();
                    lotteryShare.setOrigin(shareCustomer);
                    lotteryShare.setOwn(customer);
                    lotteryShareRepository.save(lotteryShare);
                }

            }
            return ResJson.successJson("success", null);
        } catch (JsonParseException jse) {
            logger.info(jse.getMessage() + " -> /api/cus/lottery/share");
            return ResJson.errorRequestParam(jse.getMessage() + " -> /api/cus/lottery/share");
        } catch (Exception e) {
            logger.error("/api/cus/lottery/share", e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

}
