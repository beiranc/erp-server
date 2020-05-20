package com.beiran.core.stock.entity;

import com.beiran.core.system.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 仓库实体
 */

@Getter
@Setter
@Table(name = "erp_stock")
@Entity
public class Stock {

    /**
     * 仓库编号
     */
    @GenericGenerator(name = "g_uuid", strategy = "uuid")
    @GeneratedValue(generator = "g_uuid")
    @Id
    private String stockId;

    /**
     * 仓库名
     */
    @Column(length = 50, unique = true)
    @NotBlank(message = "仓库名不能为空")
    private String stockName;

    /**
     * 仓库位置
     */
    @Column(length = 100)
    @NotBlank(message = "仓库位置不能为空")
    private String stockPosition;

    /**
     * 仓库管理员
     */
    @JoinColumn(name = "stockManagerId")
    @ManyToOne
    @NotNull(message = "仓库管理员不能为空")
    private User stockManager;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stock repo = (Stock) o;
        return Objects.equals(stockId, repo.stockId) && Objects.equals(stockName, repo.stockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockId, stockName);
    }
}
