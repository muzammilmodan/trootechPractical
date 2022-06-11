package com.trootechdemo.model

data class SubCategoryResponse(
    val `data`: ArrayList<SubCategoryResponseData>
)

data class SubCategoryResponseData(
    val categoria: Categoria,
    val categoria_ecommerce: CategoriaEcommerce,
    val codigo: String,
    val codigoBarra: String,
    val comision: String,
    val descTipoComision: String,
    val descripcion: Any,
    val idmenu: String,
    val imagen: Any,
    val impuesto: Int,
    val impuestoAplicado: Boolean,
    val modificadores: List<Modificadore>,
    val nombre: String,
    val permite_descuentos: Boolean,
    val precioSugerido: String,
    val precio_abierto: Boolean,
    val tipo: String,
    val tipo_comision: String,
    val tipo_desc: String,
    var header: Boolean,
    var precioSugeridoValues:Int = 1
)

data class Categoria(
    val codigo: String,
    val idcategoriamenu: String,
    val impuesto: String,
    val nombremenu: String,
    val orden: Any,
    val porcentaje: String,
    val printers: List<Printer>
)

data class CategoriaEcommerce(
    val codigo: String,
    val idcategoriamenu: Any,
    val impuesto: Any,
    val nombremenu: String,
    val orden: String,
    val porcentaje: Any
)

data class Modificadore(
    val idmodificador: String
)

data class Printer(
    val cutPaper: Boolean,
    val desc_printer: String,
    val desc_tipo: String,
    val id_printer: String,
    val idtipo: String,
    val ip: String,
    val isDouble: Boolean
)

