package tech.greatinfo.sellplus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import tech.greatinfo.sellplus.domain.Company;
import tech.greatinfo.sellplus.domain.coupons.Coupon;
import tech.greatinfo.sellplus.repository.CompanyRepository;
import tech.greatinfo.sellplus.repository.CouponsRepository;

/**
 *
 * Created by Ericwyn on 18-8-13.
 */
@Service
public class CompanyService {
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    CouponsRepository couponsRepository;

    public void saveMainInfo(List<Company> list){
        companyRepository.save(list);
    }

    public Company findByKey(String key){
        return companyRepository.findByK(key);
    }

    /**
     * 返回心得分享阅读数量阈值
     * @return
     */
    public int getDiaryReadNum(){
        Company company = companyRepository.findByK("diaryReadNum");
        if (company == null){
            return -1;
        }else {
            try {
                return Integer.parseInt(company.getV());
            }catch (Exception e){
                return -1;
            }
        }
    }

    /**
     * 返回心得分享奖励优惠卷
     * @return
     */
    public Coupon getDiaryCoupon(){
        Company company = companyRepository.findByK("diaryReadNum");
        if (company == null){
            return null;
        }else {
            try {
                Long couponid = Long.parseLong(company.getV());
                return couponsRepository.findOne(couponid);
            }catch (Exception e){
                return null;
            }
        }
    }

    // TODO 完成各种公司设置的 get 方法，就像上面两个方法一样
}
