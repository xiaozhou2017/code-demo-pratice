package com.example.demo.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Es 接口相关", description = "用户相关的增删改查接口") // 接口模块标签
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "创建新产品")
    @SentinelResource(value = "getUserById", blockHandler = "handleFlowException")
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product) {
        Product saved = productService.save(product);
        return ResponseEntity.ok(saved);
    }
    // 降级/限流处理方法
    public ResponseEntity handleFlowException(Long id, BlockException ex) {
        return  ResponseEntity.ok("触发熔断，服务暂时不可用");
    }
    @GetMapping
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "通过用户的唯一ID来查询其详细信息")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Iterable<Product>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.findByName(name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is running");
    }
}
