package com.labs.authentication.controller;

import com.labs.authentication.dto.Messages;
import com.labs.authentication.dto.ProductDTO;
import com.labs.authentication.entity.Product;
import com.labs.authentication.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    ProductService productService;

    @ApiOperation("Show a list of products")
    @GetMapping("/list")
    public ResponseEntity<List<Product>> list() {
        List<Product> list = productService.list();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @ApiIgnore
    @GetMapping("/detail/{id}")
    public ResponseEntity<Product> getById(@PathVariable("id") int id) {
        if (!productService.existsById(id)) {
            return new ResponseEntity(new Messages("does not exist"), HttpStatus.NOT_FOUND);
        }
        Product product = productService.getOne(id).get();
        return new ResponseEntity(product, HttpStatus.OK);
    }

    @ApiIgnore
    @GetMapping("/detailname/{name}")
    public ResponseEntity<Product> findByName(@PathVariable("name") String name) {
        if (!productService.existsByName(name))
            return new ResponseEntity(new Messages("does not exist"), HttpStatus.NOT_FOUND);
        Product product = productService.getByName(name).get();
        return new ResponseEntity(product, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProductDTO productDto) {
        if (StringUtils.isBlank(productDto.getName())) {
            return new ResponseEntity(new Messages("the name is required"), HttpStatus.BAD_REQUEST);
        }

        if (productDto.getPrice() == null || productDto.getPrice() < 0){
            return new ResponseEntity(new Messages("price must be greater than 0"), HttpStatus.BAD_REQUEST);
        }

        if (productService.existsByName(productDto.getName())){
            return new ResponseEntity(new Messages("name already exists"), HttpStatus.BAD_REQUEST);
        }

        Product product = new Product(productDto.getName(), productDto.getPrice());
        productService.save(product);
        return new ResponseEntity(new Messages("product created"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody ProductDTO productDto) {
        if (!productService.existsById(id)){
            return new ResponseEntity(new Messages("does not exist"), HttpStatus.NOT_FOUND);
        }

        if (productService.existsByName(productDto.getName())
                && productService.getByName(productDto.getName()).get().getId() != id) {
            return new ResponseEntity(new Messages("name already exists"), HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(productDto.getName())) {
            return new ResponseEntity(new Messages("name is required"), HttpStatus.BAD_REQUEST);
        }

        if (productDto.getPrice() == null || productDto.getPrice() < 0)
            return new ResponseEntity(new Messages("price must be greater than 0"), HttpStatus.BAD_REQUEST);

        Product product = productService.getOne(id).get();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        productService.save(product);
        return new ResponseEntity(new Messages("updated product"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        if (!productService.existsById(id)){
            return new ResponseEntity(new Messages("does not exist"), HttpStatus.NOT_FOUND);
        }
        productService.delete(id);
        return new ResponseEntity(new Messages("product removed"), HttpStatus.OK);
    }
}
