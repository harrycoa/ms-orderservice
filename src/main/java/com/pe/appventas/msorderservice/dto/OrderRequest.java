package com.pe.appventas.msorderservice.dto;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;


@Getter
@ApiModel(description = "Esta clase representa el proceso de pedidos")
public class OrderRequest {
    @NotNull
    @ApiModelProperty(notes = "Account ID", example = "001", required = true)
    private String accountId;

    @ApiModelProperty(notes = "Lista de items incluidos en el pedido", required = true)
    private List<LineItem> items;

}
