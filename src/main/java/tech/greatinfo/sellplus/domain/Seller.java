package tech.greatinfo.sellplus.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * 销售人员
 *
 * Created by Ericwyn on 18-8-8.
 */
@Entity
@Table(name = "seller")
public class Seller {
    private static final long serialVersionUID = -1L;

    @Id
    @GeneratedValue
    @PrimaryKeyJoinColumn
    private Long id;

    // Seller 的帐号
    @Column
    private String account;

    // Seller 的Key
    @Column
    private String sellerKey;

    // Seller 的 OPENID
    @Column
    private String openId;

    // Seller 的联系名称
    @Column
    private String name;

    // Seller 的联系电话
    @Column
    private String phone;

    public Seller() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSellerKey() {
        return sellerKey;
    }

    public void setSellerKey(String sellerKey) {
        this.sellerKey = sellerKey;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Seller
                && this.getAccount().equals(((Seller) obj).getAccount())
                && this.getOpenId().equals(((Seller) obj).getOpenId());
    }
}
