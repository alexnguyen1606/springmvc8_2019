package com.laptrinhjavaweb.repository.custom.impl;

import com.laptrinhjavaweb.builder.BuildingSearchBuilder;
import com.laptrinhjavaweb.entity.BuildingEntity;
import com.laptrinhjavaweb.repository.custom.BuildingRepositoryCustom;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Repository
public class BuildingRepositoryImpl implements BuildingRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<BuildingEntity> findAll(Map<String,Object> params, Pageable pageable,BuildingSearchBuilder fieldSearch) {
        String sqlSpecial = buildSqlSpecial(fieldSearch);

        StringBuilder sqlSearch = new StringBuilder(" Select * from building as A");
        if ( StringUtils.isNotBlank(fieldSearch.getStaffId())){
            sqlSearch.append(" INNER JOIN assignmentstaff AS am ON A.id = am.staffid");
        }
        sqlSearch.append(" WHERE 1=1");
        sqlSearch=this.createSqlFindAll(sqlSearch,params);
        sqlSearch.append(sqlSpecial);
        //System.out.println("sql Special:"+sqlSpecial);
        System.out.println("Sql search:"+sqlSearch.toString()+" end");
        Query query = entityManager.createNativeQuery(sqlSearch.toString(),BuildingEntity.class);

        return query.getResultList();
    }

    protected StringBuilder createSqlFindAll(StringBuilder where,Map<String,Object> properties) {
        if(properties!=null && properties.size()>0){
            String[] keys = new String[properties.size()];
            Object[] values = new Object[properties.size()];
            int i=0;
            for(Map.Entry<String,Object> entry : properties.entrySet()){
                keys[i]=entry.getKey();
                values[i] = entry.getValue();
                i++;
            }
            for (int i1=0 ;i1 <keys.length;i1++ ){
                if (values[i1] instanceof String && StringUtils.isNotBlank(values[i1].toString())){
                    where.append(" AND  A."+keys[i1]+" LIKE '%"+values[i1].toString()+"%' ");
                }else if(values[i1] instanceof Integer && values[i1]!=null){
                    where.append(" AND  A."+keys[i1]+" = "+values[i1]+" ");

                } else if (values[i1] instanceof Long && values[i1]!=null){
                    where.append(" AND  A."+keys[i1]+" = "+values[i1]+" ");
                }
            }
        }
        return where;
    }
    private String buildSqlSpecial(BuildingSearchBuilder fieldSearch) {
        StringBuilder sql = new StringBuilder("");
        //use java 7
        if (StringUtils.isNotBlank(fieldSearch.getCostRentForm())){
            sql.append(" AND A.costrent >= "+fieldSearch.getCostRentForm()+"");
        }
        if (StringUtils.isNotBlank(fieldSearch.getCostRentTo())){
            sql.append(" AND A.costrent <= "+fieldSearch.getCostRentTo()+"");
        }
        if (fieldSearch.getBuildingTypes().length>0){
            sql.append(" AND (");
            //java 7
//            int i=0;
//            for (String item : fieldSearch.getBuildingTypes()){
//                if (i==0){
//                    sql.append("A.type LIKE '%"+item+"%'");
//                }else {
//                    sql.append(" OR A.type LIKE '%"+item+"%'");
//                }
//                i++;
//            }
            sql.append("A.type LIKE '%"+fieldSearch.getBuildingTypes()[0]+"%'");
            Arrays.stream(fieldSearch.getBuildingTypes())
                    .filter(item -> !item.equals(fieldSearch.getBuildingTypes()[0]))
                    .forEach(item -> sql.append(" OR A.type LIKE '%"+item+"%'"));

            sql.append(")");
        }
        if (StringUtils.isNotBlank(fieldSearch.getRentAreaFrom()) || StringUtils.isNotBlank(fieldSearch.getCostRentTo()) )
        {
            sql.append(" AND EXISTS (SELECT * FROM rentarea AS ra WHERE ( ra.buildingid = A.id ");
            if (fieldSearch.getRentAreaFrom()!= null){
                sql.append(" AND ra.value >= "+fieldSearch.getRentAreaFrom()+"");
            }
            if (fieldSearch.getRentAreaTo()!= null){
                sql.append(" AND ra.value <= "+fieldSearch.getRentAreaTo()+"");
            }
            sql.append("))");
        }
        if ( StringUtils.isNotBlank(fieldSearch.getStaffId())){
            sql.append(" AND am.staffid='"+fieldSearch.getStaffId()+"'");
        }
        return sql.toString();
    }
}
