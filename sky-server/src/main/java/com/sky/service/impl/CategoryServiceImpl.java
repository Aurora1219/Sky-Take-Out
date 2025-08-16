package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.EmployeeMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增分类
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO){
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.DISABLE);
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.insert(category);
    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO){
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());

        Page<Category> page = categoryMapper.page(categoryPageQueryDTO);

        long total = page.getTotal();
        List records = page.getResult();

        return new PageResult(total,records);
    }

    /**
     * 启用禁用分类
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id){
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();

        categoryMapper.update(category);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    public void update(CategoryDTO categoryDTO){
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.update(category);
    }

    /**
     * 删除分类
     * @param id
     */
    public void deleteById(Long id){
        //categoryMapper.deleteById(id);
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            throw new RuntimeException("当前分类下关联了菜品，不能删除");
        }

        count = setmealMapper.countByCategoryId(id);
        if(count > 0){
            throw new RuntimeException("当前分类下关联了套餐，不能删除");
        }

        categoryMapper.deleteById(id);
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    public List<Category> getByType(int type){
        return categoryMapper.getByType(type);
    }
}
