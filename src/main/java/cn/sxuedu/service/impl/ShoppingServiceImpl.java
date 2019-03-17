package cn.sxuedu.service.impl;

import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.dao.ShoppingMapper;
import cn.sxuedu.pojo.Shopping;
import cn.sxuedu.service.IShoppingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShoppingServiceImpl implements IShoppingService {

    @Autowired
    ShoppingMapper shoppingMapper;
    /**
     * 地址添加
     * */
    @Override
    public ServerResponse add(Integer userId, Shopping shopping) {
        if (shopping==null){
            return ServerResponse.creatByError("参数错误");
        }
        shopping.setUserId(userId);
        int result=shoppingMapper.add(shopping);
        if (result>0){
            Map<String,Integer> map= Maps.newHashMap();
            map.put("shoppingId",shopping.getId());
            return ServerResponse.createBySuccess(map);
        }
        return ServerResponse.creatByError("添加失败");
    }

    /**
     * 商品删除
     * */
    @Override
    public ServerResponse delete(Integer userId, Integer shoppingId) {

        if (shoppingId==null){
            return ServerResponse.creatByError("shoppingId必须传");
        }
        int result=shoppingMapper.delete(shoppingId,userId);
        if (result>0){
            return ServerResponse.createBySuccess("删除成功");
        }
        return ServerResponse.creatByError("删除失败");

    }

    @Override
    public ServerResponse update(Integer userId, Shopping shopping) {

        if (shopping==null){
            return ServerResponse.creatByError("参数错误");
        }
        //防止横向越权
        shopping.setUserId(userId);
        int result=shoppingMapper.update(shopping);
        if (result>0){
            return ServerResponse.createBySuccess("地址更新成功");
        }
        return ServerResponse.creatByError("地址更新失败");
    }

    @Override
    public ServerResponse select(Integer userId, Integer shoppingId) {

        if (shoppingId==null){
            return ServerResponse.creatByError("参数错误");
        }
        Shopping shopping=shoppingMapper.selectByIdAndUserId(shoppingId,userId);
        if (shopping!=null){
            return ServerResponse.createBySuccess(shopping);
        }
        return ServerResponse.creatByError("没有该地址");
    }

    @Override
    public ServerResponse list(Integer userId, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<Shopping> shoppingList=shoppingMapper.selectAll(userId);
        PageInfo pageInfo=new PageInfo(shoppingList);

        return ServerResponse.createBySuccess(pageInfo);
    }


}