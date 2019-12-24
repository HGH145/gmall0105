package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import com.atguigu.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.atguigu.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {

        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
        return pmsBaseAttrInfos;
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        //返回平台属性值集合
        PmsBaseAttrValue pmsBaseAttrValue=new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        return pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
    }

    @Override
    public void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        String id =pmsBaseAttrInfo.getId();
        if (StringUtils.isBlank(id)){
            //id不存在，则插入存在修改
            pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
            PmsBaseAttrValue pmsBaseAttrValue= new PmsBaseAttrValue();
            List<PmsBaseAttrValue> list= pmsBaseAttrInfo.getAttrValueList();
            String AttrId =pmsBaseAttrInfo.getId();
            for (PmsBaseAttrValue p:list){
                p.setAttrId(AttrId);
                pmsBaseAttrValueMapper.insert(p);

            }

        }else {
            // id不空，修改
            // 属性修改
            Example e=new Example(PmsBaseAttrInfo.class);
            e.createCriteria().andEqualTo("id",pmsBaseAttrInfo.getId());
            pmsBaseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo,e);
            List<PmsBaseAttrValue> list= pmsBaseAttrInfo.getAttrValueList();
            // 属性值修改
            // 按照属性id删除所有属性值
            PmsBaseAttrValue pmsBaseAttrValueDel = new PmsBaseAttrValue();
            pmsBaseAttrValueDel.setAttrId(pmsBaseAttrInfo.getId());
            pmsBaseAttrValueMapper.delete(pmsBaseAttrValueDel);
            for (PmsBaseAttrValue p:list){
                p.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insertSelective(p);
            };
        };


        List<PmsBaseAttrValue> list= pmsBaseAttrInfo.getAttrValueList();
        for (PmsBaseAttrValue p:list){
            pmsBaseAttrValueMapper.updateByPrimaryKey(p);
        }
    }

}
