package com.heima.item.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.item.pojo.Item;
import com.heima.item.pojo.ItemStock;
import com.heima.item.service.IItemService;
import com.heima.item.service.IItemStockService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisHandler implements InitializingBean {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IItemService itemService;
    @Autowired
    private IItemStockService iItemStockService;

    //这个可以json序列化
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化缓存
        //1.查询商品信息
        List<Item> list = itemService.list();
        //2.放入缓存
        for (Item item : list) {
            //把item序列化为json
            String json = MAPPER.writeValueAsString(item);
            //存入redis
            stringRedisTemplate.opsForValue().set("item:id:" + item.getId(), json);
        }

        //3.查询商品信息
        List<ItemStock> stocks = iItemStockService.list();
        //4.放入缓存
        for (ItemStock stock : stocks) {
            //把item序列化为json
            String json = MAPPER.writeValueAsString(stock);
            //存入redis
            stringRedisTemplate.opsForValue().set("item:stock:id:" + stock.getId(), json);
        }
    }

    public void saveItem(Item item) {
        try {
            String json = MAPPER.writeValueAsString(item);
            stringRedisTemplate.opsForValue().set("item:id:" + item.getId(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteItemById(Long id) {
        stringRedisTemplate.delete("item:id:" + id);
    }
}
