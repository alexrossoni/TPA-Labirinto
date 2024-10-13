import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    static int[][] labirinto;
    static boolean[][] visitado;
    static int linhas, colunas;
    static JPanel[][] cells;

    // Movimentos possíveis: cima, baixo, esquerda, direita e diagonais
    static int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
    static int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};

    // Variáveis para armazenar a última célula visitada
    static int ultimoX = -1;
    static int ultimoY = -1;

    public Main(int[][] labirinto) {
        this.labirinto = labirinto;
        this.cells = new JPanel[labirinto.length][labirinto[0].length];
        initUI();
    }

    private void initUI() {
        // Configuração da janela e layout
        setTitle("Resolução de Labirinto com Animação");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(labirinto.length, labirinto[0].length));

        // Inicializa o labirinto gráfico
        for (int i = 0; i < labirinto.length; i++) {
            for (int j = 0; j < labirinto[i].length; j++) {
                cells[i][j] = new JPanel();
                if (labirinto[i][j] == 0) {
                    cells[i][j].setBackground(Color.BLACK); // Paredes são pretas
                } else {
                    cells[i][j].setBackground(Color.WHITE); // Passagens são brancas
                }
                add(cells[i][j]);
            }
        }

        setSize(600, 600); // Tamanho da janela
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setVisible(true);
    }

    public void atualizarCelula(int x, int y) {
        // coloca a cor original da ultima célula visitada
        if (ultimoX != -1 && ultimoY != -1) {
            if (labirinto[ultimoX][ultimoY] == 0) {
                cells[ultimoX][ultimoY].setBackground(Color.BLACK); // parede
            } else {
                cells[ultimoX][ultimoY].setBackground(Color.WHITE); // caminho
            }
        }

        // atualizando célula atual para vermelho
        cells[x][y].setBackground(Color.RED);

        // atualizando ultima célula visitada
        ultimoX = x;
        ultimoY = y;

        try {
            Thread.sleep(500); // velocidade da animação
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String caminhoArquivo = "labirinto.csv";

        try {
            labirinto = lerLabirintoCSV(caminhoArquivo);
            linhas = labirinto.length;
            colunas = labirinto[0].length;
            visitado = new boolean[linhas][colunas];

            Main visualizacaoLabirinto = new Main(labirinto); // interface para o labirinto

            if (visualizacaoLabirinto.resolverLabirintoComAnimacao(0, 0)) {
                System.out.println("Caminho encontrado:");
                imprimirLabirinto();
            } else {
                System.out.println("Nenhum caminho encontrado.");
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public static int[][] lerLabirintoCSV(String caminhoArquivo) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo));
        String linha;
        List<int[]> linhasLabirinto = new ArrayList<>();

        // lendo cada linha do .csv
        while ((linha = br.readLine()) != null) {
            String[] elementos = linha.split(",");
            int[] linhaLab = new int[elementos.length];

            // aqui converte os elementos em inteiros
            for (int i = 0; i < elementos.length; i++) {
                linhaLab[i] = Integer.parseInt(elementos[i]);
            }

            // adiciona a linha ao labirinto
            linhasLabirinto.add(linhaLab);
        }

        br.close();

        // aqui a lista de linhas vira uma matriz
        int[][] lab = new int[linhasLabirinto.size()][];
        for (int i = 0; i < linhasLabirinto.size(); i++) {
            lab[i] = linhasLabirinto.get(i);
        }

        return lab;
    }

    // busca em profundidade com animação para encontrar o caminho no labirinto
    public boolean resolverLabirintoComAnimacao(int x, int y) {
        // posição inválida ou visitada => retorna falso
        if (x < 0 || x >= linhas || y < 0 || y >= colunas || labirinto[x][y] == 0 || visitado[x][y]) {
            return false;
        }

        visitado[x][y] = true; // marcando a posição atual como visitada

        labirinto[x][y] = 2; // marcando o caminho na matriz

        atualizarCelula(x, y);

        if (x == linhas - 1 && y == colunas - 1) {
            return true; // chegou a saída que é a última célula da matriz
        }

        // aqui caminha as oito direções possíveis
        for (int i = 0; i < 8; i++) {
            int novoX = x + dx[i];
            int novoY = y + dy[i];
            if (resolverLabirintoComAnimacao(novoX, novoY)) {
                return true;
            }
        }

        atualizarCelula(x, y); // atualiza célula novamente

        labirinto[x][y] = 1; // retira a marcação do caminho caso seja inválido

        return false;
    }

    // apenas imprime o labirinto
    public static void imprimirLabirinto() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (labirinto[i][j] == 2) {
                    System.out.print("X ");
                } else {
                    System.out.print(labirinto[i][j] + " ");
                }
            }
            System.out.println();
        }
    }
}
