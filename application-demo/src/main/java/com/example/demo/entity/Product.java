package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Document(indexName = "newproducts")
@AllArgsConstructor // 使用Lombok生成全参构造方法
public class Product {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    @Schema(description = "产品名称", example = "智能手机")
    private String name;

    @Field(type = FieldType.Double)
    @Schema(description = "价格", example = "2999.99")
    private Double price;

    @Field(type = FieldType.Keyword)
    @Schema(description = "产品类别", example = "电子产品")
    private String category;

    @Field(type = FieldType.Text)
    @Schema(description = "产品描述", example = "这是一款最新款的智能手机")
    private String description;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    public Product() {}

    public Product(String name, Double price, String category, String description) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.createTime = LocalDateTime.now();
    }
}
