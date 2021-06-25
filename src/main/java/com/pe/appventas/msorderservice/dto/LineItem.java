package com.pe.appventas.msorderservice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Clase que representa un item incluido  en el pedido")
public class LineItem {

    @ApiModelProperty(notes = "UPC universal product code, tamaño de 12 digitos", example = "123456789000", required = true, position = 0)
    private String upc;

    @ApiModelProperty(notes = "Cantidad", example = "2", required = true, position = 1)
    private Integer  quantity;

    @ApiModelProperty(notes = "Precio", example = "17.88", required = true, position = 2)
    private Double price;

}
