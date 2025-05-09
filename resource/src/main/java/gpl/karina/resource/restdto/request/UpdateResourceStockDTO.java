package gpl.karina.resource.restdto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResourceStockDTO {
    @NotNull(message = "Jumlah stok tidak boleh kosong")
    @Min(value = 1, message = "Jumlah perubahan stok minimal 1")
    private Integer quantity;
}
