package com.thehecklers.sburrestdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/coffees")
class RestApiController {
	// 처리할 데이터
	private List<Coffee> coffees = new ArrayList<>();

	// 생성자
	public RestApiController() {
		coffees.addAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));
	}

	// GET방식으로 요청 처리...
//	@RequestMapping(value = "/coffees", method = RequestMethod.GET)
	@GetMapping
	Iterable<Coffee> getCoffees() {
		return coffees;
	}
	/*
	GetMapping, PostMapping, PutMapping, PatchMapping, DeleteMapping
	각 메서드별로 동작하는 Mapping 어노테이션

	 */
	// 특정 ID를 통해서 불러오기
	@GetMapping("/{id}")
	Optional<Coffee> getCoffee(@PathVariable String id) {  // @PathVariable은 경로값을 변수로..
		for (Coffee c : coffees) {
			if (c.getId().equals(id)) {
				return Optional.of(c);
			}
		}
		return Optional.empty();  // null
	}

	// Post는 생성하기
	@PostMapping
	Coffee postCoffee(@RequestBody Coffee c) {
		coffees.add(c);
		return c;
	}
//	Coffee postCoffee(@RequestBody Coffee coffee) {
		// @RequestBody는 요청 패킷의 데이터 부분(body)을 읽어 들임
		// 통신 요청 패킷 크게 두 부분으로 나눔. header(상태및 기타 정보), body(전달할 Data)
		// 때문에 Request Body에는 JSON형식으로 데이터를 표시해야 함.
//		System.out.println(coffee);
//		coffees.add(coffee);
//		return coffee;
//	}

	// Put 생성하기
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id , @RequestBody Coffee coffee) {
		int coffeeIndex = -1;  // coffees에 값이 없는 경우

		for (Coffee c : coffees) {
			if (c.getId().equals(id)) {   // coffees에 있는 객체 중 id가 같은 coffee가 있다면,
				coffeeIndex = coffees.indexOf(c); // coffee 객체의 인덱스값을 coffeeIndex에 대입
				coffees.set(coffeeIndex, coffee); // coffeeIndex과 전달받은 coffee 객체 정보로 수정
			}
		}
		return (coffeeIndex == -1)    // coffees 목록에 없는 경우
				? new ResponseEntity<>(postCoffee(coffee), HttpStatus.CREATED)  // 없는 경우 추가해주세요.
				: new ResponseEntity<>(coffee,HttpStatus.OK) ;			  // 수정 성공.
	}

	// delete 만들기
	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffees.removeIf(c -> c.getId().equals(id));
	}


}


// 도메인 생성 (coffee)
class Coffee {
	// 필드
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



