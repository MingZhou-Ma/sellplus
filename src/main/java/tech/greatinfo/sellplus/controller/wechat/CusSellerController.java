package tech.greatinfo.sellplus.controller.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tech.greatinfo.sellplus.domain.Customer;
import tech.greatinfo.sellplus.domain.MsgRecord;
import tech.greatinfo.sellplus.domain.Seller;
import tech.greatinfo.sellplus.domain.coupons.CouponsObj;
import tech.greatinfo.sellplus.repository.MsgRecordRepository;
import tech.greatinfo.sellplus.service.CouponsObjService;
import tech.greatinfo.sellplus.service.CustomService;
import tech.greatinfo.sellplus.service.SellerSerivce;
import tech.greatinfo.sellplus.service.TokenService;
import tech.greatinfo.sellplus.utils.GroupSmsParamUtil;
import tech.greatinfo.sellplus.utils.ParamUtils;
import tech.greatinfo.sellplus.utils.exception.JsonParseException;
import tech.greatinfo.sellplus.utils.obj.AccessToken;
import tech.greatinfo.sellplus.utils.obj.ResJson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 微信端 销售人员 相关接口
 *
 * Created by Ericwyn on 18-8-14.
 */
@RestController
public class CusSellerController {

    private static final Logger logger = LoggerFactory.getLogger(CusSellerController.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SellerSerivce sellerSerivce;

    @Autowired
    CustomService customService;

    @Autowired
    CouponsObjService objService;

    @Autowired
    MsgRecordRepository msgRecordRepository;

    @Value("${company}")
    private String company;

    @Value("${appid}")
    private String appid;

    @Value("${centerManagerSysUrl}")
    private String centerManagerSysUrl;

    /**
     * 销售登录
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/api/cus/sellerLogin",method = RequestMethod.POST)
    public ResJson sellerLogin(@RequestBody JSONObject jsonObject){
        try {
            String token = (String) ParamUtils.getFromJson(jsonObject,"token", String.class);
            String account = (String) ParamUtils.getFromJson(jsonObject,"account", String.class);
            String sellerKey = (String) ParamUtils.getFromJson(jsonObject, "sellerKey", String.class);
            Customer customer = (Customer) tokenService.getUserByToken(token);
            if (null == customer) {
                return ResJson.errorAccessToken();
            }
            Seller seller = sellerSerivce.findByAccountAndSellerKey(account, sellerKey);
            if (null == seller) {
                return ResJson.failJson(-1,"not seller",null);
            }
//            if (seller.getOpenId() == null || seller.getOpenId().equals("")) {
//                // 设置openId
//                seller.setOpenId(customer.getOpenid());
//                sellerSerivce.save(seller);

                // 成为 Seller
                customer.setbSell(true);
                customer.setSeller(seller);
                //customer.setUid(customer.getUid());
                customService.save(customer);

                AccessToken accessToken = tokenService.getToken(token);
                if (null != accessToken) {
                    accessToken.setUser(customer);
                    tokenService.saveToken(accessToken);
                }
                return ResJson.successJson("seller login success", customer);
//            } else {
//                return ResJson.failJson(4000, "此销售账号已被登录", null);
//            }




        }catch (JsonParseException jse){
            logger.info(jse.getMessage()+" -> /api/cus/sellerLogin");
            return ResJson.errorRequestParam(jse.getMessage()+" -> /api/cus/sellerLogin");
        }catch (Exception e){
            logger.error("/api/cus/sellerLogin -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    /**
     * 绑定成为 Seller，这个接口其实就是销售登录，暂时没用到。
     * POST
     *      token
     *      account 销售人帐号
     *      key     销售人帐号的key
     *      name    名称
     *      phone   销售人员电话号码
     *      wechat  微信号码
     *      intro   销售人员介绍
     *      avatar  头像的 pic 地址
     *
     * @param jsonObject
     * @return
     */
//    @RequestMapping(value = "/api/cus/beSeller",method = RequestMethod.POST)
//    public ResJson beSeller(@RequestBody JSONObject jsonObject){
//        try {
//            String token = (String) ParamUtils.getFromJson(jsonObject,"token", String.class);
//            String selleAccount = (String) ParamUtils.getFromJson(jsonObject,"account", String.class);
//            String key =    (String) ParamUtils.getFromJson(jsonObject, "key", String.class);
//            /*String name =   (String) ParamUtils.getFromJson(jsonObject, "name", String.class);
//            String phone =  (String) ParamUtils.getFromJson(jsonObject, "phone", String.class);
//            String wechat = (String) ParamUtils.getFromJson(jsonObject, "wechat", String.class);
//            String intro =  (String) ParamUtils.getFromJson(jsonObject, "intro", String.class);
//            String pic =    (String) ParamUtils.getFromJson(jsonObject, "avatar", String.class);*/
//
//            // 该帐号是公司帐号，默认销售
//            // 公司简介
//           Customer customer ;
//            if ((customer = (Customer) tokenService.getUserByToken(token)) != null){
//                Seller seller;
//                if ((seller = sellerSerivce.findByAccountAndSellerKey(selleAccount, key)) != null
//                        && (seller.getOpenId() == null || seller.getOpenId().equals(""))){
//                    // 保存 seller 信息
//                    seller.setOpenId(customer.getOpenid());
//                    /*seller.setName(name);
//                    seller.setPhone(phone);
//                    seller.setWechat(wechat);
//                    seller.setIntro(intro);
//                    seller.setPic(WeChatUtils.getBigAvatarURL(pic));*/
//                    sellerSerivce.save(seller);
//
//                    // 插入默认销售渠道
//                    /*SellerCode sellerCode = new SellerCode();
//                    sellerCode.setName("默认渠道");
//                    sellerCode.setPath("");
//                    sellerCode.setCustomer(customer);*/
//
//                    // 成为 Seller
//                    customer.setbSell(true);
//                    customer.setSeller(seller);
//                    customer.setUid(customer.getUid());
//                    customService.save(customer);
//                    return ResJson.successJson("set seller info success");
//                }else {
//                    return ResJson.failJson(-1,"seller info error",null);
//                }
//            }else {
//                return ResJson.errorAccessToken();
//            }
//        }catch (JsonParseException jse){
//            logger.info(jse.getMessage()+" -> /api/cus/beSeller");
//            return ResJson.errorRequestParam(jse.getMessage()+" -> /api/cus/beSeller");
//        }catch (Exception e){
//            logger.error("/api/cus/beSeller -> ",e.getMessage());
//            e.printStackTrace();
//            return ResJson.serverErrorJson(e.getMessage());
//        }
//    }

    /**
     *
     * 绑定上级 Seller
     *
     * 所有人默认的绑定都是id为 1 的默认 seller
     * 而 seller 本身，在成为 seller 的时候，绑定的 seller 就是自己了
     *
     * 所有人的分享页面都带有自己帐号的 uuid，前端发送 token 和 uid 来调用绑定接口
     * 若是某个页面没有 uid 的话，该参数需要发送为 “null”
     *
     * 回溯寻找 Seller 的时候， 只会回溯到 uid 的用户， 如果 uid 的用户没有绑定 Seller
     *      那么当前用户和 uid 用户都会绑定默认的 Seller
     *
     * 如果 uid 的用户已经绑定了非默认 Seller ， 那么当前用户就绑定同一个 Seller
     *
     * 如果之前已经绑定了默认 Seller，当前可以绑定新的 Seller
     * 如果之前已经有绑定 Seller ， 那么不做处理
     *
     * POST
     *      token   当前用户的 token
     *      uuid    上一级用户的 uuid
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/api/cus/bindSeller",method = RequestMethod.POST)
    public ResJson bindSeller(@RequestBody JSONObject jsonObject){
        try {
            String token = (String) ParamUtils.getFromJson(jsonObject,"token", String.class);
            String uid = (String) ParamUtils.getFromJsonWithDefault(jsonObject,"uid", "null", String.class);

            String sellerCode = (String) ParamUtils.getFromJsonWithDefault(jsonObject, "sellerCode", "", String.class);
            //String sellerCode = (String) ParamUtils.getFromJson(jsonObject, "sellerCode", String.class);
            //初次访问记录
            //String accessRecord = (String) ParamUtils.getFromJsonWithDefault(jsonObject, "accessRecord", "null", String.class);

            if (uid.equals("null")){
                return ResJson.successJson("uid is null");
            }
            Customer customer;
            if ((customer = (Customer) tokenService.getUserByToken(token)) != null){
                // 已经绑定了非默认 Seller
                if (customer.getSeller() != null
                        && !customer.getSeller().equals(sellerSerivce.getDefaultSeller())){
                    return ResJson.successJson("已经绑定了非默认 Seller", customer.getSeller());
                }else {
                    Seller seller;
                    Customer preCustomer;
                    if ((preCustomer = customService.getByUid(uid)) != null){
                        if ((seller = preCustomer.getSeller()) != null){
                            //记录销售渠道
                            /*if (StringUtils.isEmpty(customer.getSellerChannel())) {
                                customer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                            } else {
                                // 判断是否绑定了默认销售的某个渠道，是的话要进行替换
                                if (customer.getSeller().equals(sellerSerivce.getDefaultSeller())) {
                                    customer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                                }
                            }*/

                            customer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                            customer.setSeller(seller);
                            customService.save(customer);

                            AccessToken accessToken = tokenService.getToken(token);
                            accessToken.setUser(customer);
                            tokenService.saveToken(accessToken);

                            return ResJson.successJson("success bind seller", seller);
                        }else {
                            //记录销售渠道
                            /*if (StringUtils.isEmpty(preCustomer.getSellerChannel())) {
                                preCustomer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                            } else {
                                // 判断是否绑定了默认销售的某个渠道，是的话要进行替换
                                if (preCustomer.getSeller().equals(sellerSerivce.getDefaultSeller())) {
                                    preCustomer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                                }
                            }*/
                            preCustomer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                            preCustomer.setSeller(sellerSerivce.getDefaultSeller());
                            customService.save(preCustomer);

                            AccessToken preAccessToken = tokenService.getTokenByCustomOpenId(preCustomer.getOpenid());
                            preAccessToken.setUser(preCustomer);
                            tokenService.saveToken(preAccessToken);

                            //记录销售渠道
                            /*if (StringUtils.isEmpty(customer.getSellerChannel())) {
                                customer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                            } else {
                                // 判断是否绑定了默认销售的某个渠道，是的话要进行替换
                                if (customer.getSeller().equals(sellerSerivce.getDefaultSeller())) {
                                    customer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                                }
                            }*/

                            customer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                            customer.setSeller(sellerSerivce.getDefaultSeller());
                            customService.save(customer);

                            AccessToken accessToken = tokenService.getToken(token);
                            accessToken.setUser(customer);
                            tokenService.saveToken(accessToken);

                            return ResJson.successJson("success bind seller", sellerSerivce.getDefaultSeller());
                        }
                    }else {
                        //记录销售渠道
                        /*if (StringUtils.isEmpty(customer.getSellerChannel())) {
                            customer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                        } else {
                            // 判断是否绑定了默认销售的某个渠道，是的话要进行替换
                            if (customer.getSeller().equals(sellerSerivce.getDefaultSeller())) {
                                customer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                            }
                        }*/

                        customer.setSellerChannel("".equals(sellerCode)?"":uid + "|" + sellerCode);
                        customer.setSeller(sellerSerivce.getDefaultSeller());

                        customService.save(customer);

                        AccessToken accessToken = tokenService.getToken(token);
                        accessToken.setUser(customer);
                        tokenService.saveToken(accessToken);

                        return ResJson.successJson("success bind seller", sellerSerivce.getDefaultSeller());
                    }
                }
                //return null;
            }else {
                return ResJson.errorAccessToken();
            }
        }catch (JsonParseException jse){
            logger.info(jse.getMessage()+" -> /api/cus/bindSeller");
            return ResJson.errorRequestParam(jse.getMessage()+" -> /api/cus/bindSeller");
        }catch (Exception e){
            logger.error("/api/cus/bindSeller -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    /**
     * 查看自己的 Seller 信息
     *
     * POST
     *      token
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/api/cus/showMySeller",method = RequestMethod.POST)
    public ResJson showMySeller(@RequestBody JSONObject jsonObject){
        try {
            String token = (String) ParamUtils.getFromJson(jsonObject,"token", String.class);
            Customer customer;
            if ((customer = (Customer) tokenService.getUserByToken(token)) != null){
                Seller res;
                if ((res = customer.getSeller()) == null){
                    res = sellerSerivce.getDefaultSeller();
                }
                return ResJson.successJson("get seller info success", res);
            }else {
                return ResJson.errorAccessToken();
            }
        }catch (JsonParseException jse){
            logger.info(jse.getMessage()+" -> /api/cus/bindSeller");
            return ResJson.errorRequestParam(jse.getMessage()+" -> /api/cus/bindSeller");
        }catch (Exception e){
            logger.error("/api/cus/bindSeller -> ",e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    /**
     *
     * 微信端 seller 核销优惠卷
     *
     * POST
     *      token   微信销售用户登录的 token
     *      code    优惠卷的 code
     *
     * @return
     */
    @RequestMapping(value = "/api/cus/writeOffCoupons", method = RequestMethod.POST)
    public ResJson getCustomerNews(@RequestBody JSONObject jsonObject) {
        try {
            String token = (String) ParamUtils.getFromJson(jsonObject,"token", String.class);
            String couponCode = (String) ParamUtils.getFromJson(jsonObject,"code", String.class);
            Customer customer;
            if ((customer = (Customer) tokenService.getUserByToken(token)) != null){
                if (!customer.getbSell()){
                    return ResJson.failJson(-1,"没有 seller 权限",null);
                }
                CouponsObj coupon = objService.findByCode(couponCode);
                if (coupon == null){
                    return ResJson.failJson(-1,"优惠卷代码错误",null);
                }else {
                    if (coupon.getExpired()) {
                        return ResJson.failJson(4000,"已经核销过", null);
                    }
                    // 核销优惠卷, 如果微信已经注册成为 seller 的话，那么绑定的 seller 就是他的上级 seller
                    objService.writeOffCoupons(coupon,customer.getSeller());
                    return  ResJson.successJson("write off coupon success");
                }
            }else {
                return ResJson.errorAccessToken();
            }
        } catch (Exception e) {
            logger.error("/api/cus/writeOffCoupons -> ", e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }


    /**
     *
     * 微信端 seller 获取自己的用户
     *
     * POST
     *      token   微信销售用户登录的 token
     *      start   开始页数默认为 0
     *      num     分页每页的信息数量默认为 999
     *
     * @return
     */
    @RequestMapping(value = "/api/cus/getAllMyCustomer", method = RequestMethod.POST)
    public ResJson getAllMyCustomer(@RequestBody JSONObject jsonObject) {
        try {
            String token = (String) ParamUtils.getFromJson(jsonObject,"token", String.class);
            Integer start = (Integer) ParamUtils.getFromJsonWithDefault(jsonObject,"start", 0, Integer.class);
            Integer num = (Integer) ParamUtils.getFromJsonWithDefault(jsonObject,"num", 999 ,Integer.class);
            Customer customer;
            if ((customer = (Customer) tokenService.getUserByToken(token)) != null){
                System.out.println(customer.getOpenid());
                if (!customer.getbSell()){
                    return ResJson.failJson(-1,"没有 seller 权限",null);
                }
                Page<Customer> allBySeller = customService.getAllBySeller(customer.getSeller(), new PageRequest(start, num));
                List<Customer> list = customService.findBySeller(customer.getSeller().getId());
                int phoneNumbers = 0;
                for (Customer c : list) {
                    if (StringUtils.isNotEmpty(c.getPhone())) {
                        phoneNumbers++;
                    }
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("customerList", allBySeller);
                map.put("phoneNumbers", phoneNumbers);
                return ResJson.successJson("get all my customer success",map);
            }else {
                return ResJson.errorAccessToken();
            }
        } catch (Exception e) {
            logger.error("/api/cus/writeOffCoupons -> ", e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    @RequestMapping(value = "/api/cus/syncContact", method = RequestMethod.POST)
    public ResJson syncContact(@RequestBody JSONObject jsonObject) {
        try {
            String token = (String) ParamUtils.getFromJson(jsonObject,"token", String.class);
            Long customerId = (Long) ParamUtils.getFromJson(jsonObject,"customerId", Long.class);

            Customer customer = (Customer) tokenService.getUserByToken(token);
            if (null == customer) {
                return ResJson.errorAccessToken();
            }

            Customer sellerCustomer = customService.getOne(customerId);
            if (null == sellerCustomer) {
                return ResJson.failJson(4000, "客户不存在", null);
            }
            if (sellerCustomer.getbSync()) {
                return ResJson.failJson(4000, "已经同步到通讯录", null);
            }
            sellerCustomer.setbSync(true);
            customService.save(sellerCustomer);

            return ResJson.successJson("同步成功");
        } catch (Exception e) {
            logger.error("/api/cus/writeOffCoupons -> ", e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

    @RequestMapping(value = "/api/cus/group/msg", method = RequestMethod.POST)
    public ResJson groupMsg(@RequestBody JSONObject jsonObject) {
        try {
            String token = (String) ParamUtils.getFromJson(jsonObject,"token", String.class);
            String content = (String) ParamUtils.getFromJson(jsonObject,"content", String.class);

            Customer customer = (Customer) tokenService.getUserByToken(token);
            if (null == customer) {
                return ResJson.errorAccessToken();
            }
            if (!customer.getbSell()) {
                return ResJson.failJson(4000, "您不是销售", null);
            }

            List<Customer> list = customService.findBySeller(customer.getSeller().getId());
            List<String> phoneList = new ArrayList<>();
            List<GroupSmsParamUtil> paramList = new ArrayList<>();
            List<String> signNameList = new ArrayList<>();
            if (null != list && !list.isEmpty()) {
                for (Customer c : list) {
                    if (StringUtils.isNotEmpty(c.getPhone())) {
                        phoneList.add(c.getPhone());
                        signNameList.add("获客Plus");
                        GroupSmsParamUtil groupSmsParamUtil = new GroupSmsParamUtil();
                        groupSmsParamUtil.setContent("提示：" + content + "。");
                        groupSmsParamUtil.setCompany (company);
                        paramList.add(groupSmsParamUtil);
                    }
                }
            }
            String phone = JSONObject.toJSONString(phoneList);
            String signName = JSONObject.toJSONString(signNameList);
            String param = JSONObject.toJSONString(paramList);
            //发送短信
//            if (!SendGroupSmsUtil.sendMulSms(phone, signName, param)) {
//                return ResJson.failJson(4000, "group msg fail", null);
//            }

            JSONObject json = new JSONObject();
            json.put("phone", phone);
            json.put("signName", signName);
            json.put("param", param);
            json.put("appId", appid);
            json.put("numbers", phoneList.size());
            //创建一个OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(centerManagerSysUrl + "/api/sms/send")
                    .post(okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toJSONString()))
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body() != null ? response.body().string() : null;
                System.out.println(result);
                // 如果短信发送成功，则继续执行
                JSONObject obj = JSON.parseObject(result);
                String code = obj.getString("code");
                if (code.equals("1000")) {
                    MsgRecord msgRecord = new MsgRecord();
                    msgRecord.setNum(phoneList.size());
                    msgRecord.setContent(content);
                    msgRecord.setSendTime(new Date());
                    msgRecord.setCustomer(customer);
                    msgRecordRepository.save(msgRecord);

                    return ResJson.successJson("group msg success");
                } else {
                    return ResJson.successJson("group msg fail");
                }

            } else {
                return ResJson.failJson(4000, "请求发送短信接口失败", null);
            }

        } catch (Exception e) {
            logger.error("/api/cus/group/msg -> ", e.getMessage());
            e.printStackTrace();
            return ResJson.serverErrorJson(e.getMessage());
        }
    }

}
