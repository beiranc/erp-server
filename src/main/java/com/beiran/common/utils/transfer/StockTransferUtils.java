package com.beiran.common.utils.transfer;

import com.beiran.core.stock.dto.StockDto;
import com.beiran.core.stock.entity.Stock;
import com.beiran.core.system.dto.UserSmallDto;
import com.beiran.core.system.entity.User;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * 提供 Stock-StockDto 互相转换的工具
 */

public class StockTransferUtils {

    /**
     * 将 Stock 转换为 StockDto
     * @param stock 需要转换的 Stock
     * @return StockDto
     */
    public static StockDto stockToDto(Stock stock) {
        StockDto stockDto = new StockDto();
        if (!Objects.equals(stock, null)) {
            BeanUtils.copyProperties(stock, stockDto);
        }
        UserSmallDto userSmallDto = new UserSmallDto();
        if (!Objects.equals(stock.getStockManager(), null)) {
            userSmallDto.setUserId(stock.getStockManager().getUserId());
            userSmallDto.setUserName(stock.getStockManager().getUserName());
        }
        stockDto.setManager(userSmallDto);
        return stockDto;
    }

    /**
     * 将 StockDto 转换为 Stock
     * @param stockDto 需要转换的 StockDto
     * @return Stock
     */
    public static Stock dtoToStock(StockDto stockDto) {
        Stock stock = new Stock();
        if (!Objects.equals(stockDto, null)) {
            BeanUtils.copyProperties(stockDto, stock);
        }
        User user = new User();
        if (!Objects.equals(stockDto.getManager(), null)) {
            user.setUserId(stockDto.getManager().getUserId());
            user.setUserName(stockDto.getManager().getUserName());
        }
        stock.setStockManager(user);
        return stock;
    }
}
