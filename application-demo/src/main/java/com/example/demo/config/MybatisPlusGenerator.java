package com.example.demo.config;


import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.Objects;

public class MybatisPlusGenerator {

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
//        String projectPath = "";
        // 获取当前项目根路径
        String projectPath = System.getProperty("user.dir");
        System.out.println("项目路径: " + projectPath);

// 如果要生成到 src/main/java 目录
        String outputDir = projectPath + "/src/main/java";
        gc.setFileOverride(true);
        gc.setActiveRecord(true);// 不需要ActiveRecord特性的请改为false
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(false);// XML columList
        gc.setKotlin(false); //是否生成 kotlin 代码

        gc.setOutputDir(outputDir);
        gc.setFileOverride(true);
        gc.setAuthor("mark zhou");
        gc.setOpen(false);
       // gc.setDateType(DateType.TIME_PACK);

      //  gc.setEntityName("%s");
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("I%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        mpg.setGlobalConfig(gc);



        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setTypeConvert(new MySqlTypeConvert(){
            // 自定义数据库表字段类型转换【可选】
            @Override
            public DbColumnType processTypeConvert(String fieldType) {
                System.out.println("转换类型：" + fieldType);
                String t = fieldType.toLowerCase();
                if (t.contains("clob") || t.contains("blob") || t.contains("binary")) {
                    return DbColumnType.STRING;
                } else if (Objects.equals("date",t)) {
                    return DbColumnType.LOCAL_DATE;
                }  else if (Objects.equals("datetime",t) || Objects.equals("timestamp", t)) {
                    return DbColumnType.LOCAL_DATE_TIME;
                }  else if (Objects.equals("time",t)) {
                    return DbColumnType.LOCAL_TIME;
                } else {
                    // 注意！！processTypeConvert 存在默认类型转换，如果不是你要的效果请自定义返回、非如下直接返回。
                    return super.processTypeConvert(fieldType);
                }
            }
        });
        dsc.setUrl("jdbc:mysql://127.0.0.1:3306/duomipay?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=GMT%2B8");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 包含的表
       // strategy.setInclude("sequence","sys_dept","sys_dic","sys_log","sys_menu","sys_param","sys_role","sys_role_menu","sys_user","sys_user_menu","sys_user_role");
        //strategy.setInclude("sso_application","sso_user","sso_user_app");
        //strategy.setInclude("settlement_apply","settlement_apply_admission","settlement_order_fail_record","settlement_order_record");
        // strategy.setInclude("prox_third","proxy_pay_account","proxy_pay_account_flow","proxy_pay_apply","proxy_pay_recharge");
      // strategy.setInclude("pay_account","pay_account_channel","pay_backcard","pay_bank_transfer_amount","pay_collect_qrcode","pay_collect_qrcode_using","pay_collect_server","pay_collect_transfer_qrcode","pay_customer","pay_orderinfo");
        strategy.setInclude("sys_user");
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.nochange);
        //strategy.setSuperEntityClass("com.baomidou.ant.common.BaseEntity");
        //strategy.setEntityLombokModel(true);
        //strategy.setRestControllerStyle(true);
        //strategy.setSuperControllerClass("com.baomidou.ant.common.BaseController");

        // strategy.setSuperEntityColumns("id");
        //strategy.setControllerMappingHyphenStyle(true);
        //strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("pay");
        pc.setParent("com.example.demo");
        pc.setEntity("entity");
        pc.setMapper("mapper");
        pc.setXml("mapper");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setController("controller");
        mpg.setPackageInfo(pc);

        // 自定义配置
//        InjectionConfig cfg = new InjectionConfig() {
//            @Override
//            public void initMap() {
//                // to do nothing
//            }
//        };

        // 如果模板引擎是 freemarker
        //String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
//        List<FileOutConfig> focList = new ArrayList<>();
//        // 自定义配置会被优先输出
//        focList.add(new FileOutConfig(templatePath) {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
//                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
//                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
//            }
//        });
//
//        cfg.setFileOutConfigList(focList);
//        mpg.setCfg(cfg);

        // 配置模板
//        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

//        templateConfig.setXml(null);
//        mpg.setTemplate(templateConfig);


       // mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
