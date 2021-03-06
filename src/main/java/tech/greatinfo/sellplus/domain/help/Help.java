package tech.greatinfo.sellplus.domain.help;

import tech.greatinfo.sellplus.domain.Activity;
import tech.greatinfo.sellplus.domain.Customer;

import javax.persistence.*;

/**
 *
 * 好友助力表
 *
 * Created by Ericwyn on 18-7-23.
 */
@Entity
@Table(name = "helpAct")
public class Help {
    @Id
    @GeneratedValue
    @PrimaryKeyJoinColumn
    private Long id;

    //    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_id",columnDefinition = "BIGINT COMMENT '活动外键'")
    private Activity activity;

    //    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id",columnDefinition = "BIGINT COMMENT '发起人外键'")
    private Customer customer;

    @Transient
    private Integer isOwner;

    @Transient
    private Integer isHelp;

    @Transient
    private Integer helpCount;

    @Column(name = "general", columnDefinition = "BIT COMMENT '是否已经兑换优惠卷'")
    private boolean general;

    public Help() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Integer isOwner) {
        this.isOwner = isOwner;
    }

    public Integer getIsHelp() {
        return isHelp;
    }

    public void setIsHelp(Integer isHelp) {
        this.isHelp = isHelp;
    }

    public Integer getHelpCount() {
        return helpCount;
    }

    public void setHelpCount(Integer helpCount) {
        this.helpCount = helpCount;
    }

    public boolean isGeneral() {
        return general;
    }

    public void setGeneral(boolean general) {
        this.general = general;
    }
}
