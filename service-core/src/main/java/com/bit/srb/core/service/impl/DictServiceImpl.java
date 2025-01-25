package com.bit.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.srb.core.listener.ExcelDictDTOListener;
import com.bit.srb.core.mapper.DictMapper;
import com.bit.srb.core.pojo.dto.ExcelDictDTO;
import com.bit.srb.core.pojo.entity.Dict;
import com.bit.srb.core.service.DictService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    private RedisTemplate redisTemplate;
    @Autowired
    private DictMapper dictMapper;

    @Transactional(rollbackFor = Exception.class) // 所有异常出现时都回滚
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("import successfully");
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = baseMapper.selectList(null);
        // 创建ExcelDictDTO
        ArrayList<ExcelDictDTO> excelDictDTOList = new ArrayList<>(dictList.size());
        dictList.forEach(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict,excelDictDTO);
            // 检测相同的名称并进行复制
            excelDictDTOList.add(excelDictDTO);
        });
        return excelDictDTOList;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {
        // 查询redis有没有数据列表，有就直接返回，否则查询数据库
        // 按Parentid分类
        try {
            List<Dict> dictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList" + parentId);
            if(dictList != null){
                log.info("从Redis里获取了数据列表");
                return dictList;
            }
        } catch (Exception e){
            log.error("redis服务器异常：" + ExceptionUtils.getStackTrace(e));
        }
        //Redis服务器异常时不抛出异常，只报错，然后去数据库里取数据

        // 如果查询了数据库 就再把数据放在Redis里
        log.info("从数据库里获取了数据列表");
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",parentId);
        List<Dict> dictList = baseMapper.selectList(dictQueryWrapper);
        // 设定hasChildren
        dictList.forEach(dict -> {
            Boolean hasChildren = this.hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        });
        try{
            log.info("数据存入了Redis");
            redisTemplate.opsForValue().set("srb:core:dictList" + parentId, dictList,5, TimeUnit.MINUTES);
        } catch (Exception e){
            log.error("redis服务器异常：" + ExceptionUtils.getStackTrace(e));
        }


        return dictList;
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);
        return this.listByParentId(dict.getId());
    }

    @Override
    public String getNameByValueAndDictCode(Integer value, String dictCode) {
        // ！！！！


        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code", dictCode);
        Dict dict = dictMapper.selectOne(dictQueryWrapper);

        if(dict == null)
            return "";

        dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id", dict.getId()).eq("value", value);
        Dict dict1 = dictMapper.selectOne(dictQueryWrapper);

        if(dict1 == null)
            return "";

        return dict1.getName();
    }

    // 根据当前id搜索表里其他的parentid看是否有一样的，判断当前id是否还有子id
    private Boolean hasChildren(Long id){
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",id);
        Long count = baseMapper.selectCount(dictQueryWrapper);
        if( count > 0){
            return true;
        }
        else
            return false;
    }
}
