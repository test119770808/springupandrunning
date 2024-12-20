package com.thehecklers.sburrestdemo;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class SburRestDemoApplication {

	public static void main(String[] args) {
        SpringApplication.run(SburRestDemoApplication.class, args);
	}
}

// Ch4 5.초기 데이터를 생성하는 컴포넌트 구현
@Component
class DataLoader {
	private final CoffeeRepository coffeeRepository;
	public DataLoader(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	@PostConstruct
	private void loadData() {
		this.coffeeRepository.saveAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));
	}
}


@RestController
@RequestMapping("/coffees")
class RestApiController {
	// Ch4 3.저장소 사용하기
//	// 처리할 데이터
//	private List<Coffee> coffees = new ArrayList<>();  // 삭제
	// 저장소 호출(spring의 생성자 주입)
	private final CoffeeRepository coffeeRepository;

	// 생성자(수정) -> coffeeRepository 불러옴. 생성장에서 초기 데이터 생성
	public RestApiController(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;

		//Ch4 5. 이전 추가 정보 정리하기...
//		this.coffeeRepository.saveAll(List.of(
//				new Coffee("Cafe Cereza"),
//				new Coffee("Cafe Ganador"),
//				new Coffee("Cafe Lareno"),
//				new Coffee("Cafe Tres Pontas")
//		));

	}

	// Ch4 4. 매핑된 메서드 수정작업...
//	@RequestMapping(value = "/coffees", method = RequestMethod.GET)
	@GetMapping
	Iterable<Coffee> getCoffees() {
		return coffeeRepository.findAll();
	}
	/*
	GetMapping, PostMapping, PutMapping, PatchMapping, DeleteMapping
	각 메서드별로 동작하는 Mapping 어노테이션

	 */
	// 특정 ID를 통해서 불러오기
	@GetMapping("/{id}")
	Optional<Coffee> getCoffee(@PathVariable String id) {  // @PathVariable은 경로값을 변수로..
		return coffeeRepository.findById(id);
	}

	// Post는 생성하기
	@PostMapping
	Coffee postCoffee(@RequestBody Coffee c) {
		return coffeeRepository.save(c);
	}

	// Put 생성하기
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id , @RequestBody Coffee coffee) {
		// 1. id를 이용해서 객체가 있는지 확인
		// 2. 존재여부에 따라서 있으면, 전달받은 coffee 객체를 이용해서 수정
		//   없으면, 전달받은 coffee 객체를 이용해서 새롭게 생성하면 됨.
		return (coffeeRepository.existsById(id))    // coffees 목록에 없는 경우
				? new ResponseEntity<>(coffeeRepository.save(coffee),HttpStatus.OK)       // 수정 성공.
				: new ResponseEntity<>(coffeeRepository.save(coffee),HttpStatus.CREATED);  // 없는 경우 추가해주세요.
	}

	// delete 만들기
	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffeeRepository.deleteById(id);
	}
}

// Ch4 데이터베이스 액세스 작업
// 2. 저장소(repository) - coffee 엔티티에 대한
interface CoffeeRepository extends CrudRepository<Coffee, String> {}


// 1. 도메인 생성 (coffee)- ch4에서 Entity 구성
@Entity(name = "coffees")
class Coffee {
	// 필드
	// id에 primary key 설정
	@Id
	private String id;
	private String name;

	// 생성자
	// jackson-databind에서 버전 2.18.1 버전에서 (2.14.1 버전에서는 오류가 없었음)
	// id를 final로 설정하고 작업한 경우에 binding처리에 오류가 발생
	// 해결 방법 - final을 제거, 기본 생성자 생성, id에 대한 setter 생성 후 작업
	public Coffee() {}
	public Coffee(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Coffee(String name){
		this(UUID.randomUUID().toString(),name);
	}

	// Getter, Setter
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setId(String id) {
		this.id = id;
	}
}



