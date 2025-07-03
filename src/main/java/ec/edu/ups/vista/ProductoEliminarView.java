package ec.edu.ups.vista;

import javax.swing.*;


public class ProductoEliminarView extends JInternalFrame{
    private JTextField codigo;
    private JTextField nombre;
    private JTextField precio;
    private JButton buscarButton;
    private JButton eliminarButton;
    private JPanel panelPrincipal;

    public ProductoEliminarView() {
    setContentPane(panelPrincipal);
    setTitle("Eliminacion de producto");
    setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    setSize(500, 500);
    setClosable(true);
    setIconifiable(true);
    setResizable(true);
    }



    public JTextField getTextField1() {
        return codigo;
    }

    public void setTextField1(JTextField textField1) {
        this.codigo = textField1;
    }

    public JTextField getTextField2() {
        return nombre;
    }

    public void setTextField2(JTextField textField2) {
        this.nombre = textField2;
    }

    public JTextField getTextField3() {
        return precio;
    }

    public void setTextField3(JTextField textField3) {
        this.precio = textField3;
    }

    public JButton getBuscarButton() {
        return buscarButton;
    }

    public void setBuscarButton(JButton buscarButton) {
        this.buscarButton = buscarButton;
    }

    public JButton getEliminarButton() {
        return eliminarButton;
    }

    public void setEliminarButton(JButton eliminarButton) {
        this.eliminarButton = eliminarButton;
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    public void setPanelPrincipal(JPanel panelPrincipal) {
        this.panelPrincipal = panelPrincipal;
    }

    public boolean configurarEliminacion(){
        int opcion = JOptionPane.showConfirmDialog(this,
                "Está seguro que quiere eliminar el producto?",
                "Confirmar Eliminacion",
                JOptionPane.YES_NO_OPTION);
        return opcion == JOptionPane.YES_OPTION;
    }
    public void limpiarCampos(){
        codigo.setText("");
        nombre.setText("");
        precio.setText("");
    }


}



