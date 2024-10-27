import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ListaSequencial
{
    // Arquivo padrão contendo o CSV, se não receber por parâmetro.
    private static final String DEFAULT_DB = "/tmp/pokemon.csv";

    public static void main(String[] args) throws Exception
    {
        String arquivo = (args.length > 0) ? args[0] : DEFAULT_DB;
        List<Pokemon> pokemon = new GerenciadorPokemons(arquivo).getPokemons();
        ListaPokemon lista = new ListaPokemon(801);

        // Lê da entrada padrão.
        try (Scanner sc = new Scanner(System.in)) {
            String input;

            // Adiciona os Pokémon selecionados à lista.
            while (!(input = sc.nextLine()).equals("FIM"))
                lista.inserirFim(pokemon.get(Integer.parseInt(input) - 1));

            // Lê os comandos de inserção e remoção da lista.
            while (sc.hasNext()) {
                Pokemon temp = null;
                String comando = sc.next();

                if (comando.equals("II")) {
                    lista.inserirInicio(pokemon.get(sc.nextInt() - 1));
                } else if (comando.equals("I*")) {
                    int pos = sc.nextInt();
                    int pokeIdx = sc.nextInt() - 1;
                    lista.inserir(pokemon.get(pokeIdx), pos);
                } else if (comando.equals("IF")) {
                    lista.inserirFim(pokemon.get(sc.nextInt() - 1));
                } else if (comando.equals("RI")) {
                    temp = lista.removerInicio();
                } else if (comando.equals("R*")) {
                    temp = lista.remover(sc.nextInt());
                } else if (comando.equals("RF")) {
                    temp = lista.removerFim();
                }

                if (temp != null)
                    System.out.println("(R) " + temp.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Imprime a lista resultante.
        for (int i = 0; i < lista.size(); ++i)
            System.out.println("[" + i + "] " + lista.get(i));
    }
}

class ListaPokemon
{
    private Pokemon[] arr;
    private int n;

    public ListaPokemon(int capacidade)
    {
        arr = new Pokemon[capacidade];
        n = 0;
    }

    public void inserir(Pokemon x, int pos) throws Exception
    {
        if (pos < 0 || pos > n)
            throw new Exception("Posição " + pos + " é inválida.");
        if (n == arr.length)
            throw new Exception("O arranjo da lista está cheio.");

        for (int i = n; i > pos; --i)
            arr[i] = arr[i - 1];

        arr[pos] = x;
        n += 1;
    }

    public void inserirInicio(Pokemon x) throws Exception
    {
        this.inserir(x, 0);
    }

    public void inserirFim(Pokemon x) throws Exception
    {
        this.inserir(x, n);
    }

    public Pokemon remover(int pos) throws Exception
    {
        if (pos < 0 || pos >= n)
            throw new Exception("Posição " + pos + " é inválida.");
        if (n == 0)
            throw new Exception("A lista está vazia.");

        Pokemon res = arr[pos];

        for (int i = pos + 1; i < n; ++i)
            arr[i - 1] = arr[i];

        n -= 1;
        return res;
    }

    public Pokemon removerInicio() throws Exception
    {
        return this.remover(0);
    }

    public Pokemon removerFim() throws Exception
    {
        return this.remover(n - 1);
    }

    public int size()
    {
        return n;
    }

    public Pokemon get(int pos) throws Exception
    {
        if (pos < 0 || pos >= n)
            throw new Exception("Posição " + pos + " é inválida.");
        if (n == 0)
            throw new Exception("A lista está vazia.");

        return arr[pos];
    }

    public void set(int pos, Pokemon x) throws Exception
    {
        if (pos < 0 || pos >= n)
            throw new Exception("Posição " + pos + " é inválida.");

        arr[pos] = x;
    }
}

class GerenciadorPokemons
{
    private List<Pokemon> pokemons;

    public GerenciadorPokemons()
    {
        this(801);
    }

    public GerenciadorPokemons(int n)
    {
        pokemons = new ArrayList<Pokemon>(n);
    }

    public GerenciadorPokemons(String arquivo) throws FileNotFoundException
    {
        this(0);
        this.lerCsv(arquivo);
    }

    public void lerCsv(String arquivo) throws FileNotFoundException
    {
        try (Scanner csvScanner = new Scanner(new File(arquivo))) {
            // Descarta a primeira linha (cabeçalho).
            csvScanner.nextLine();

            // Lê cada linha do CSV e cria um Pokémon.
            while (csvScanner.hasNextLine())
                pokemons.add(new Pokemon(csvScanner.nextLine()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
    }

    public List<Pokemon> getPokemons()
    {
        return pokemons;
    }
}

class Pokemon implements Comparable<Pokemon>, Cloneable
{
    private int id, generation, captureRate;
    private String name, description;
    private List<PokeType> types;
    private List<String> abilities;
    private double weight, height;
    private boolean isLegendary;
    private LocalDate captureDate; // Os métodos de Date são deprecados.

    private static int numComparacoes = 0; // Para contar comparações.

    public Pokemon()
    {
        this.id = 0; // Chave padrão.
        this.generation = 0; // Geração padrão.
        this.name = "Desconhecido"; // Nome padrão.
        this.description = "Sem descrição"; // Descrição padrão.
        this.types = new ArrayList<>(); // Lista de tipos vazia.
        this.abilities = new ArrayList<>(); // Lista de habilidades vazia.
        this.weight = 0.0; // Peso padrão.
        this.height = 0.0; // Altura padrão.
        this.captureRate = 0; // Taxa de captura padrão.
        this.isLegendary = false; // Não é lendário por padrão.
        this.captureDate = LocalDate.MIN; // Data nula (01/01/-9999999…)
    }

    public Pokemon(int id, int generation, String name, String description,
                   List<PokeType> types, List<String> abilities, double weight,
                   double height, int captureRate, boolean isLegendary,
                   LocalDate captureDate)
    {
        this.id = id;
        this.generation = generation;
        this.name = name;
        this.description = description;
        this.types = types;
        this.abilities = abilities;
        this.weight = weight;
        this.height = height;
        this.captureRate = captureRate;
        this.isLegendary = isLegendary;
        this.captureDate = captureDate;
    }

    public Pokemon(String str)
    {
        this.ler(str);
    }

    public void ler(String str) throws ArrayIndexOutOfBoundsException
    {
        // Três seções principais da String de entrada: os elementos antes das
        // habilidades, a lista de habilidades em si, e os elementos após as
        // habilidades.
        String[] sec = str.split("\"");

        // Separa as seções em si em elementos individuais.
        String[] s1 = sec[0].split(",");
        String[] s2 = sec[2].split(",");

        // Lê os elementos iniciais.
        id = Integer.parseInt(s1[0]);
        generation = Integer.parseInt(s1[1]);
        name = s1[2];
        description = s1[3];

        // Adiciona os tipos. Usamos o .valueOf() do enum para facilitar.
        types = new ArrayList<>();
        types.add(PokeType.valueOf(s1[4].toUpperCase()));
        // Se tiver segundo tipo, adiciona-o.
        if (s1.length > 5 && !s1[5].isEmpty())
            types.add(PokeType.valueOf(s1[5].toUpperCase()));

        // Remove os caracteres extra da lista de abilidades e as adiciona.
        abilities = new ArrayList<>();
        for (String a : sec[1].split(", ")) {
            a = a.replace("[", "").replace("]", "").replace("'", "");
            if (!a.isEmpty())
                abilities.add(a);
        }

        // Adiciona peso e altura. Se estiverem vazios, devem ser 0.
        String weightStr = s2[1];
        String heightStr = s2[2];
        weight = weightStr.isEmpty() ? 0 : Double.parseDouble(weightStr);
        height = heightStr.isEmpty() ? 0 : Double.parseDouble(heightStr);

        // Lê o determinante da probabilidade de captura e se é lendário ou não.
        captureRate = Integer.parseInt(s2[3]);
        isLegendary = (Integer.parseInt(s2[4]) == 1);

        // Adiciona data de captura.
        String[] membrosData = s2[5].split("/");
        captureDate = LocalDate.of(Integer.parseInt(membrosData[2]), // ano
                                   Integer.parseInt(membrosData[1]), // mês
                                   Integer.parseInt(membrosData[0])); // dia
    }

    public void imprimir()
    {
        System.out.println(this);
    }

    @Override public String toString()
    {
        String res = new String("[#");
        res +=
            id + " -> " + name + ": " + description + " - ['" +
            types.get(0).toString().toLowerCase() +
            ((types.size() == 2) ? "', '" + types.get(1).toString().toLowerCase() : "") +
            "'] - ['" + abilities.get(0) + "'";

        for (int i = 1; i < abilities.size(); ++i)
            res += ", '" + abilities.get(i) + "'";

        res += "] - " + weight + "kg - " + height + "m - " + captureRate + "% - " +
               isLegendary() + " - " + generation + " gen] - " +
               String.format("%02d/%02d/%04d", captureDate.getDayOfMonth(),
                             captureDate.getMonthValue(), captureDate.getYear());

        return res;
    }

    // Ordena Pokémon por geração.
    @Override public int compareTo(Pokemon outro)
    {
        ++numComparacoes;
        int res = Integer.valueOf(generation).compareTo(outro.getGeneration());
        return res != 0 ? res : name.compareTo(outro.getName());
    }

    @Override public Pokemon clone()
    {
        try {
            Pokemon c = (Pokemon)super.clone();

            // Copia as listas, que são referências (Strings também são
            // referências, mas são imutáveis, então não é necessário).
            c.types = new ArrayList<>(this.types);
            c.abilities = new ArrayList<>(this.abilities);

            return c;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new AssertionError(); // Nunca deve acontecer.
        }
    }

    // Getters e Setters.

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getGeneration()
    {
        return generation;
    }

    public void setGeneration(int generation)
    {
        this.generation = generation;
    }

    public int getCaptureRate()
    {
        return captureRate;
    }

    public void setCaptureRate(int captureRate)
    {
        this.captureRate = captureRate;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<PokeType> getTypes()
    {
        return types;
    }

    public void setTypes(List<PokeType> types)
    {
        this.types = types;
    }

    public List<String> getAbilities()
    {
        return abilities;
    }

    public void setAbilities(List<String> abilities)
    {
        this.abilities = abilities;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public boolean isLegendary()
    {
        return isLegendary;
    }

    public void setLegendary(boolean isLegendary)
    {
        this.isLegendary = isLegendary;
    }

    public LocalDate getCaptureDate()
    {
        return captureDate;
    }

    public void setCaptureDate(LocalDate captureDate)
    {
        this.captureDate = captureDate;
    }

    public static int getNumComparacoes()
    {
        return numComparacoes;
    }

    public static void setNumComparacoes(int numComparacoes)
    {
        Pokemon.numComparacoes = numComparacoes;
    }

    // Tipos de Pokémon.
    static enum PokeType {
        BUG,
        DARK,
        DRAGON,
        ELECTRIC,
        FAIRY,
        FIGHTING,
        FIRE,
        FLYING,
        GHOST,
        GRASS,
        GROUND,
        ICE,
        NORMAL,
        POISON,
        PSYCHIC,
        ROCK,
        STEEL,
        WATER
    }
}
