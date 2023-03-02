package com.m.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m.productservice.dto.ProductRequest;
import com.m.productservice.model.Product;
import com.m.productservice.repository.ProductRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void mongoDbProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}
	@AfterEach
	void deleteProducts(){
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();

		String productRequestJSON = objectMapper.writeValueAsString(productRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestJSON))
				.andExpect(status().isCreated());

		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("Bosch Microwave")
				.description("Bosch Microwave")
				.price(BigDecimal.valueOf(350))
				.build();
	}

	@Test
	void givenProducts_whenGetAllProducts_thenReturnOk() throws Exception {

		List<Product> products =
				List.of(Product.builder().name("Microwave").description("Microwave").price(BigDecimal.valueOf(350)).build(),
				Product.builder().name("Keyboard").description("Keyboard").price(BigDecimal.valueOf(220)).build());
		productRepository.saveAll(products);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/products"));

		response.andExpect(status().isOk());
		response.andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(products.size())));
	}
}
