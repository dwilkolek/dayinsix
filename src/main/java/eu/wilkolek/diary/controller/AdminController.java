package eu.wilkolek.diary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.model.Meta;
import eu.wilkolek.diary.repository.ErrorRepository;
import eu.wilkolek.diary.repository.MetaRepository;
import eu.wilkolek.diary.service.MetaService;


@Controller
public class AdminController {

    @Autowired
    private MetaRepository metaRepository;
    
    @Autowired
    private ErrorRepository errorRepository;
    
    @Autowired
    private MetaService metaService;
    
    
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/meta", method = RequestMethod.GET)
    public ModelAndView getMetas(){
        ModelAndView model = new ModelAndView("admin/meta");
        
        
        model.getModelMap().put("metas",metaRepository.findAll());
        
        return model;
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/meta/add", method = RequestMethod.GET)
    public ModelAndView addMeta(){
        ModelAndView model = new ModelAndView("admin/metaAdd");
                
        return model;
    }
    
    
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/meta/{id}", method = RequestMethod.GET)
    public ModelAndView getEdit(@PathVariable(value = "id") String id){
        ModelAndView model = new ModelAndView("admin/metaEdit");
        
        Meta meta = metaRepository.findOne(id);
        model.getModelMap().put("meta", meta);
        
        return model;
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/meta/save", method = RequestMethod.POST)
    public String saveMeta(@RequestParam(value="description", required=false)String description, @RequestParam(value="keywords", required=false)String keywords, @RequestParam(value="title", required=false)String title,@RequestParam(value="id", required=false) String id){
        
        Meta meta = metaRepository.findOne(id);
        meta.setDescription(description);
        meta.setKeywords(keywords);
        meta.setTitle(title);
        metaService.updateMeta(meta);
        
             
        
        return "redirect:/admin/meta";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/meta/save/new", method = RequestMethod.POST)
    public String saveMetaNew(@RequestParam(value="url", required=false)String url,@RequestParam(value="description", required=false)String description, @RequestParam(value="keywords", required=false)String keywords, @RequestParam(value="title", required=false)String title,@RequestParam(value="id", required=false) String id){
        
        Meta meta = new Meta(url, title, description, keywords);
      
        metaService.updateMeta(meta);
        
             
        
        return "redirect:/admin/meta";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/error", method = RequestMethod.GET)
    public ModelAndView errors(){
        ModelAndView model = new ModelAndView("admin/errors");
        
       model.getModelMap().put("errors", errorRepository.findAll());
        
             
        
        return model;
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/error/{id}", method = RequestMethod.GET)
    public ModelAndView viewError(@PathVariable(value="id")String id){
        ModelAndView model = new ModelAndView("admin/errorDetails");
        eu.wilkolek.diary.model.Error e = errorRepository.findOne(id);
       model.getModelMap().put("error", e);
        
             
        
        return model;
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/error/{id}/delete", method = {RequestMethod.POST,RequestMethod.GET})
    public String deleteError(@PathVariable(value="id")String id){
        errorRepository.delete(id);
        return "redirect:/admin/error";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/error/clean", method = {RequestMethod.POST,RequestMethod.GET})
    public String clean(){
        errorRepository.deleteAll();
        System.out.println("Cleaned all errors");
        return "redirect:/admin/error";
    }
    
}
