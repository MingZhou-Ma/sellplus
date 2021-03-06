package tech.greatinfo.sellplus.domain;

import javax.persistence.*;

/**
 * 公司风采、学员风采
 */
@Entity
@Table(name = "style")
public class Style {
    @Id
    @GeneratedValue
    @PrimaryKeyJoinColumn
    private Long id;

    /*@Column(columnDefinition = "VARCHAR(255) COMMENT '标题'")
    private String title;*/

    @Column(columnDefinition = "VARCHAR(255) COMMENT '图片地址'")
    private String pic;

    @Column(columnDefinition = "TEXT COMMENT '描述(长)'")
    private String description;

    @Column(columnDefinition = "INT COMMENT '风采类型'")
    private Integer type;

    @Column(columnDefinition = "INT COMMENT '随机数'")
    private Integer number;

//    @Column(columnDefinition = "TIMESTAMP COMMENT '时间'")
//    private Date time;

    public Style() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
