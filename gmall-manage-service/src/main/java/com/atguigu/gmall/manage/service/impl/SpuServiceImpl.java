package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsProductSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsProductImageMapper;
import com.atguigu.gmall.manage.mapper.PmsProductInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.atguigu.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.atguigu.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    PmsProductImageMapper pmsProductImageMapper;
    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;
    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        Example example=new Example(PmsProductInfo.class);
        example.createCriteria().andEqualTo("catalog3Id",catalog3Id);
        List<PmsProductInfo> list=pmsProductInfoMapper.selectByExample(example);
        return list;
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {

        //保存spu数据
        pmsProductInfoMapper.insertSelective(pmsProductInfo);

        //传入数据为空，但插入数据后会自动返回id的值SPU的id作为子类的productId传入下级并插入
        String productId=pmsProductInfo.getId();
        //查询传入图片集合，插入图片类PmsProductImage
        List<PmsProductImage> pmsProductImages=pmsProductInfo.getSpuImageList();
        for (PmsProductImage pi:pmsProductImages){
            //设置productid
            pi.setProductId(productId);
            pmsProductImageMapper.insertSelective(pi);
        }

        //查询传入SKU集合，插入SKU类PmsProductSaleAttr
        List<PmsProductSaleAttr> PmsProductSaleAttrS=pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr ps:PmsProductSaleAttrS){
            //设置productid
            ps.setProductId(productId);
            //获取sku下的属性集合，并插入到PmsProductSaleAttrValue
            List<PmsProductSaleAttrValue> PmsProductSaleAttrValues= ps.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pav:PmsProductSaleAttrValues){
               pmsProductSaleAttrValueMapper.insertSelective(pav);
            }
            pmsProductSaleAttrMapper.insertSelective(ps);
        }
    }
}
