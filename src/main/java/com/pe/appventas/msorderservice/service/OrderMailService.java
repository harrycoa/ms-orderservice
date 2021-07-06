package com.pe.appventas.msorderservice.service;

import com.pe.appventas.msorderservice.dto.ShipmentOrderResponse;
import com.pe.appventas.msorderservice.entities.Order;
import com.pe.appventas.msorderservice.util.OrderUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderMailService {
    @Autowired
    private JavaMailSender sender;

    @Autowired
    private Configuration freemarkerConfig;

    public void sendEmail(Order order, ShipmentOrderResponse response) throws Exception {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        Map<String, Object> model = new HashMap();
        model.put("user", response.getName());
        model.put("orderId", order.getOrderId());
        model.put("orderStatus", order.getStatus().name());
        model.put("orderTotal", OrderUtils.formatDecimal(order.getTotalAmountIgv()));
        model.put("orderTrackingId", response.getTrackingId());

        String shippingDate = response.getShippingDate() != null ?
                String.valueOf(response.getShippingDate()) : "En Proceso.";
        String deliveryDate = response.getDeliveredDate() != null ?
                String.valueOf(response.getDeliveredDate()) : "En Transito.";

        model.put("orderShippingDate", shippingDate);
        model.put("orderDeliveryDate", deliveryDate);
        model.put("orderAddress", OrderUtils.obtainFullAddress(response.getAddress()));

        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/");

        Template t = freemarkerConfig.getTemplate("email.ftl");
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

        helper.setTo(response.getReceiptEmail());
        helper.setText(text, true);
        helper.setSubject("Tu Pedido es el:  #" + order.getOrderId());

        sender.send(message);
    }
}
