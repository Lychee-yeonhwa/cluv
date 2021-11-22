package com.shop.controller;


import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping(value = "/order")
    //Spring에서 비동기 처리 -> @RequestBody와 @ResponseBody 사용
    //@RequestBody : HTTP 요청의 본문 body에 담긴 내용을 자바 객체로 전달
    //@ResponseBody : 자바 객체를 HTTP 요청의 boby로 전달
    public @ResponseBody ResponseEntity order
            (@RequestBody @Valid OrderDto orderDto,
                          BindingResult bindingResult, Principal principal){

        //주문 정보 받는 orderDto 객체에 데이터 바인딩시 에러가 있는지 검사
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErros = bindingResult.getFieldErrors();
            for ( FieldError fieldError  : fieldErros) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity(sb.toString(), HttpStatus.BAD_REQUEST);
            //에러 정보를 ResponseEntity 객체에 담아서 반환
       }

        //현재 로그인 유저 정보 얻기
        //@Controller 선언된 클래스에서 메소드 인자로
        //principal 객체 넘겨줄 경우 해당 객체 직접 접근 가능
        //principal 객체에서 현재 로그인한 회원의 이메일 정보 조회
        String email = principal.getName();
        Long orderId;

        try{
            //화면으로부터 넘어오는 주문 정보와 회원의 이메일 정보 이용 -> 주문 로직 호출
            orderId = orderService.order(orderDto, email);
        } catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),
            HttpStatus.BAD_REQUEST);
        }

        //결과값으로 생성된 주문 번호와 요청 성공했다는 HTTP응답 상태 코드 반환
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);

    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page,
                            Principal principal, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);  // 1.

        //2.
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page" , pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId , Principal principal){

        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
