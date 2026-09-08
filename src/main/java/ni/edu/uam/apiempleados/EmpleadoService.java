package ni.edu.uam.apiempleados;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EmpleadoService {
    private final List<EmpleadoDTO> empleados = new ArrayList<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    public List<EmpleadoDTO> listar() {
        return empleados;
    }

    public Optional<EmpleadoDTO> buscarPorId(Long id) {
        return empleados.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    // Comprueba si ya existe alguien con el mismo nombre y apellido (sin distinguir mayúsculas/minúsculas)
    public boolean existeEmpleado(String nombres, String apellidos) {
        return empleados.stream().anyMatch(e ->
                e.getNombres().equalsIgnoreCase(nombres.trim()) &&
                        e.getApellidos().equalsIgnoreCase(apellidos.trim())
        );
    }

    public EmpleadoDTO guardar(EmpleadoDTO empleado) {
        if (existeEmpleado(empleado.getNombres(), empleado.getApellidos())) {
            throw new IllegalArgumentException("Ya existe un empleado registrado con esos nombres y apellidos");
        }
        empleado.setId(secuencia.incrementAndGet());
        empleados.add(empleado);
        return empleado;
    }

    public Optional<EmpleadoDTO> actualizar(Long id, EmpleadoDTO datos) {
        return buscarPorId(id).map(empleado -> {
            boolean mismoNombre = empleado.getNombres().equalsIgnoreCase(datos.getNombres().trim()) &&
                    empleado.getApellidos().equalsIgnoreCase(datos.getApellidos().trim());

            if (!mismoNombre && existeEmpleado(datos.getNombres(), datos.getApellidos())) {
                throw new IllegalArgumentException("Ya existe otro empleado registrado con esos nombres y apellidos");
            }

            empleado.setNombres(datos.getNombres());
            empleado.setApellidos(datos.getApellidos());
            empleado.setCargo(datos.getCargo());
            empleado.setSalario(datos.getSalario());
            return empleado;
        });
    }

    public boolean eliminar(Long id) {
        return empleados.removeIf(e -> e.getId().equals(id));
    }
}