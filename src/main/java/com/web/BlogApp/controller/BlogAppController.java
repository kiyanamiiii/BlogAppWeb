package com.web.BlogApp.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.BlogApp.dtos.BlogAppRecordDto;
import com.web.BlogApp.model.PostModel;
import com.web.BlogApp.service.BlogAppService;

import jakarta.validation.Valid;

@Controller
public class BlogAppController {

	@Autowired
	BlogAppService blogappservice;

	// Add root path redirection
	@GetMapping("/")
	public String rootRedirect() {
		return "redirect:/posts";
	}

	// LISTA TODOS OS POSTS
	@GetMapping(value = "/posts")
	public ModelAndView getPosts() {
		ModelAndView mv = new ModelAndView("posts");
		List<PostModel> posts = blogappservice.findAll();
		mv.addObject("post", posts);
		return mv;
	}

	// MOSTRA DETALHES DE UM POST ESPECÍFICO
	@GetMapping(value = "/posts/{id}")
	public ModelAndView getPostDetails(@PathVariable UUID id) {
		ModelAndView mv = new ModelAndView("postDetails");
		Optional<PostModel> post = blogappservice.findById(id);

		if (post.isPresent()) {
			mv.addObject("post", post.get());
		}

		return mv;
	}

	// PREPARAR FORMULÁRIO PARA NOVO POST
	@GetMapping(value = "/newpost")
	public String getPostForm(Model model) {
		model.addAttribute("postDto", new BlogAppRecordDto(null, null, null));
		return "newpostForm";
	}

	// SALVAR NOVO POST
	@PostMapping(value = "/newpost")
	public String savePost(@Valid @ModelAttribute("postDto") BlogAppRecordDto postDto,
						   BindingResult bindingResult,
						   RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "newpostForm";
		}

		PostModel post = new PostModel();
		post.setAutor(postDto.autor());
		post.setTitulo(postDto.titulo());
		post.setTexto(postDto.texto());
		post.setData(LocalDate.now());

		blogappservice.save(post);

		redirectAttributes.addFlashAttribute("message", "Post created successfully!");
		return "redirect:/posts";
	}

	// DELETE POST
	@GetMapping("/posts/delete/{id}")
	public String deletePost(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		Optional<PostModel> post = blogappservice.findById(id);

		if (post.isPresent()) {
			blogappservice.delete(post.get());
			redirectAttributes.addFlashAttribute("message", "Post deleted successfully!");
		}

		return "redirect:/posts";
	}
}