package com.example.demo.proyecto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.proyecto.model.Producto;

public interface repositoryProducto extends JpaRepository<Producto, Long> {
       /**
        * Busca productos cuyo nombre contenga la cadena proporcionada, 
        * ignorando mayúsculas y minúsculas. Ideal para la barra de búsqueda.
        * @param nombre Texto a buscar en el nombre del producto.
        * @return Lista de productos coincidentes.
        */
       List<Producto> findByNombreContainingIgnoreCase(String nombre);

       /**
        * Elimina todos los productos cuyo ID sea mayor al especificado.
        * Utilizado principalmente para limpieza de datos de prueba o resets rápidos.
        * @param id ID a partir del cual borrar (exclusivo).
        */
       void deleteByIdGreaterThan(Long id);

       /**
        * Obtiene una lista con todas las categorías únicas existentes en la base de datos,
        * excluyendo valores nulos o vacíos. Sirve para poblar los filtros dinámicos del frontend.
        * @return Lista de nombres de categorías.
        */
       @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL AND p.categoria != ''")
       List<String> findDistinctCategorias();

       /**
        * Obtiene una lista con todos los supermercados únicos existentes en la base de datos,
        * excluyendo valores nulos o vacíos. Sirve para poblar los filtros dinámicos del frontend.
        * @return Lista de nombres de supermercados.
        */
       @Query("SELECT DISTINCT p.supermercado FROM Producto p WHERE p.supermercado IS NOT NULL AND p.supermercado != ''")
       List<String> findDistinctSupermercados();

       /**
        * Borra la tabla intermedia producto_categorias en caso de que existan registros huérfanos.
        * Útil para operaciones de mantenimiento o reestructuración de la base de datos.
        */
       @org.springframework.data.jpa.repository.Modifying
       @org.springframework.transaction.annotation.Transactional
       @Query(value = "DROP TABLE IF EXISTS producto_categorias CASCADE", nativeQuery = true)
       void limpiarTablasHuerfanas();

       /**
        * Elimina todos los productos asociados a un supermercado específico.
        * @param supermercado Nombre del supermercado cuyos productos se eliminarán.
        */
       @org.springframework.transaction.annotation.Transactional
       void deleteBySupermercado(String supermercado);

       /**
        * Obtiene una proyección ligera con solo los nombres y supermercados de todos los productos.
        * Utilizado intensivamente por el DataSeeder para verificar duplicados en memoria al importar
        * miles de productos desde archivos CSV sin cargar la entidad completa.
        * @return Lista de arrays de objetos [nombre, supermercado].
        */
       @Query("SELECT p.nombre, p.supermercado FROM Producto p")
       List<Object[]> findAllNombresYSupermercados();
}