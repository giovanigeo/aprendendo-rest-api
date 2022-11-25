package com.lojaapi.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lojaapi.model.ProdutoModel;
import com.lojaapi.repository.ProdutoRepository;

@RestController
public class ProdutoController {

	@Autowired
	ProdutoRepository repository;
	
	@GetMapping("/produtos")
	public ResponseEntity<List<ProdutoModel>> getAllProdutos(){
		
		List<ProdutoModel> produtLista = repository.findAll();
		if(!produtLista.isEmpty()) {
			for(ProdutoModel p: produtLista) {
				UUID id = p.getId();
				p.add(linkTo(methodOn(ProdutoController.class).getOneProduto(id)).withSelfRel());
			}
		}
		
		return new ResponseEntity<List<ProdutoModel>>(produtLista, HttpStatus.OK);
	}
	
	@GetMapping("/produtos/{id}")
	public ResponseEntity<ProdutoModel> getOneProduto(@PathVariable(value = "id") UUID id){
		Optional<ProdutoModel> produtoOp = repository.findById(id);
		if(produtoOp.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		produtoOp.get().add(linkTo(methodOn(ProdutoController.class)
				.getAllProdutos()).withRel("Lista de Produtos"));
		return new ResponseEntity<ProdutoModel>(produtoOp.get(), HttpStatus.OK);
	}
	
	@PostMapping("/produtos")
	public ResponseEntity<ProdutoModel> saveProduto(@RequestBody @Validated ProdutoModel produto){
		return new ResponseEntity<ProdutoModel>(repository.save(produto), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/produtos/{id}")
	public ResponseEntity<ProdutoModel> deleteProduto (@PathVariable(value="id") UUID id){
		Optional<ProdutoModel> produtoOp = repository.findById(id);
		if(produtoOp.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		repository.delete(produtoOp.get());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/produtos/{id}")
	public ResponseEntity<ProdutoModel> updateProduto (@PathVariable(value = "id") UUID id,  @RequestBody @Validated ProdutoModel produto){
		Optional<ProdutoModel> produtoOp = repository.findById(id);
		if(produtoOp.isEmpty()) {
			new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		produto.setId(produtoOp.get().getId());
		return new ResponseEntity<ProdutoModel>(repository.save(produto), HttpStatus.OK);
	}
	
	
}
