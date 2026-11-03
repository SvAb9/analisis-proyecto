# Caso de estudio R1: similitud textual

Generado por `CasoEstudioR1` con el código del proyecto. Abstract A = artículo 11, abstract B = artículo 16.

- **A (11)**: A scoping review on how generative artificial intelligence transforms assessment in higher education
- **B (16)**: Generative Artificial Intelligence in Tertiary Education: Assessment Redesign Principles and Considerations

## Preparación

| | A | B |
|---|---|---|
| caracteres normalizados | 1350 | 1072 |
| tokens de contenido | 115 | 93 |

## Levenshtein

Matriz D para las palabras iniciales "generative" (A) y "emergence" (B):

| | ε | e | m | e | r | g | e | n | c | e |
|---|---|---|---|---|---|---|---|---|---|---|
| ε | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
| g | 1 | 1 | 2 | 3 | 4 | 4 | 5 | 6 | 7 | 8 |
| e | 2 | 1 | 2 | 2 | 3 | 4 | 4 | 5 | 6 | 7 |
| n | 3 | 2 | 2 | 3 | 3 | 4 | 5 | 4 | 5 | 6 |
| e | 4 | 3 | 3 | 2 | 3 | 4 | 4 | 5 | 5 | 5 |
| r | 5 | 4 | 4 | 3 | 2 | 3 | 4 | 5 | 6 | 6 |
| a | 6 | 5 | 5 | 4 | 3 | 3 | 4 | 5 | 6 | 7 |
| t | 7 | 6 | 6 | 5 | 4 | 4 | 4 | 5 | 6 | 7 |
| i | 8 | 7 | 7 | 6 | 5 | 5 | 5 | 5 | 6 | 7 |
| v | 9 | 8 | 8 | 7 | 6 | 6 | 6 | 6 | 6 | 7 |
| e | 10 | 9 | 9 | 8 | 7 | 7 | 6 | 7 | 7 | 6 |

D[10][9] = 6

Abstracts completos: distancia = 947, similitud = 1 - 947 / 1350 = 0.2985

## Needleman-Wunsch

Matriz F para los 6 primeros tokens de contenido de cada abstract:

| | ε | emergence | generative | artificial | intelligence | ai | chatgpt |
|---|---|---|---|---|---|---|---|
| ε | 0 | -1 | -2 | -3 | -4 | -5 | -6 |
| generative | -1 | -1 | 0 | -1 | -2 | -3 | -4 |
| artificial | -2 | -2 | -1 | 1 | 0 | -1 | -2 |
| intelligence | -3 | -3 | -2 | 0 | 2 | 1 | 0 |
| provides | -4 | -4 | -3 | -1 | 1 | 1 | 0 |
| opportunities | -5 | -5 | -4 | -2 | 0 | 0 | 0 |
| challenges | -6 | -6 | -5 | -3 | -1 | -1 | -1 |

```
-             generative    artificial    intelligence  provides      opportunities challenges    
emergence     generative    artificial    intelligence  -             ai            chatgpt       
```

Puntaje = -1, coincidencias = 3 de 7

Abstracts completos: puntaje F[n][m] = -101, coincidencias = 8, longitud = 117, similitud = 0.0684

Tokens alineados como coincidencia: [generative, artificial, intelligence, assessment, education, assessment, education, assessment]

## Jaccard

- |A| = 82 — términos únicos de A: [activities, ai, analyses, analysis, approach, articles, artificial, assessment, beliefs, challenges, cultivate, development, digital, disciplinary, education, educational, empirical, existing, extension, forward, generative, higher, holistic, human, ii, iii, impact, innovative, institutions, integrity, intelligence, inter, investigate, investigated, learning, levels, literacy, literature, opportunities, policies, prisma, professional, programs, properly, provide, provided, provides, published, recommended, reflect, regulated, report, responsible, results, rethink, retrieved, review, reviews, scoping, screening, select, selected, self, skills, strengthened, students, studies, successfully, suggested, teacher, teachers, teaching, technology, thinking, three, took, transform, transformation, transformed, transforms, used, well]
- |B| = 69 — términos únicos de B: [aaa, academic, acts, address, adopt, affordances, ai, argues, arms, around, article, artificial, assessment, assessments, avoid, biased, challenges, chatgpt, cheating, concerns, consider, considerations, considers, critical, discriminatory, education, emerge, emergence, engaging, existence, exponentially, fundamental, generative, hallucination, hence, illustrates, information, integrity, intelligence, internet, issues, language, large, largely, made, models, nonetheless, outputs, plagiarism, policing, principle, produce, proposes, provide, race, redesign, rethink, retrieved, revolved, significant, sparked, specific, students, technologies, tertiary, tools, trained, untruthful, use]
- |A ∩ B| = 12 — términos comunes: [ai, artificial, assessment, challenges, education, generative, integrity, intelligence, provide, rethink, retrieved, students]
- |A ∪ B| = 139 — 82 + 69 - 12
- resultado = 0.08633093525179857 — 12 / 139

## Coseno TF-IDF

IDF sobre los N = 20 abstracts. Términos comunes (los únicos que aportan al producto punto):

| término | fA | tfA | fB | tfB | df | idf | wA | wB | wA·wB |
|---|---|---|---|---|---|---|---|---|---|
| ai | 2 | 0.0174 | 7 | 0.0753 | 18 | 1.1001 | 0.0191 | 0.0828 | 0.0016 |
| artificial | 2 | 0.0174 | 1 | 0.0108 | 20 | 1.0000 | 0.0174 | 0.0108 | 0.0002 |
| assessment | 8 | 0.0696 | 3 | 0.0323 | 10 | 1.6466 | 0.1145 | 0.0531 | 0.0061 |
| challenges | 1 | 0.0087 | 1 | 0.0108 | 6 | 2.0986 | 0.0182 | 0.0226 | 0.0004 |
| education | 4 | 0.0348 | 2 | 0.0215 | 18 | 1.1001 | 0.0383 | 0.0237 | 0.0009 |
| generative | 2 | 0.0174 | 7 | 0.0753 | 4 | 2.4351 | 0.0423 | 0.1833 | 0.0078 |
| integrity | 1 | 0.0087 | 1 | 0.0108 | 2 | 2.9459 | 0.0256 | 0.0317 | 0.0008 |
| intelligence | 2 | 0.0174 | 1 | 0.0108 | 20 | 1.0000 | 0.0174 | 0.0108 | 0.0002 |
| provide | 1 | 0.0087 | 1 | 0.0108 | 10 | 1.6466 | 0.0143 | 0.0177 | 0.0003 |
| rethink | 1 | 0.0087 | 1 | 0.0108 | 2 | 2.9459 | 0.0256 | 0.0317 | 0.0008 |
| retrieved | 1 | 0.0087 | 1 | 0.0108 | 2 | 2.9459 | 0.0256 | 0.0317 | 0.0008 |
| students | 2 | 0.0174 | 1 | 0.0108 | 8 | 1.8473 | 0.0321 | 0.0199 | 0.0006 |

- N = 20.0000
- A·B = 0.0204
- ||A|| = 0.2920
- ||B|| = 0.3758
- cos θ = 0.1864
- θ = 79.2598

CosenoTfIdf.comparar(A, B) = 0.1864; sin corpus (IDF solo con A y B): 0.2102

## Embeddings de oración (all-MiniLM-L6-v2)

- modelo — all-MiniLM-L6-v2, dimensión 384
- A — 233 tokens WordPiece en 1 ventana(s), mean pooling; primeras componentes [-0.0713, 0.0167, 0.0047, 0.0100, -0.0383, -0.0095, -0.0455, -0.0009, ...]
- B — 194 tokens WordPiece en 1 ventana(s), mean pooling; primeras componentes [-0.0985, -0.0041, 0.0579, -0.0224, 0.0035, -0.0115, -0.0235, -0.0367, ...]
- A·B = 0.7354903500418903 — suma de a_i·b_i sobre 384 componentes
- ||A|| = 0.9999999999999998 — raíz de la suma de a_i²
- ||B|| = 1.000000000000001 — raíz de la suma de b_i²
- cos θ = 0.7354903500418897 — 0.7355 / (1.0000 · 1.0000)
- θ = 42.65133370475647 — arccos(cos θ) en grados
- d(A, B) = 0.7273371294772601 — raíz de la suma de (a_i - b_i)²

## Embeddings de palabras promediados (GloVe)

- modelo — glove.6B.100d.corpus.txt, dimensión 100
- A — promedio de 115 de 115 palabras con vector; primeras componentes [-0.0703, 0.2667, -0.0590, 0.2133, 0.0179, -0.0965, -0.1598, 0.0791, ...]
- B — promedio de 92 de 93 palabras con vector; primeras componentes [-0.0835, 0.2641, 0.1889, 0.1685, -0.0400, -0.0912, -0.2150, -0.0978, ...]
- A·B = 7.593424901563041 — suma de a_i·b_i sobre 100 componentes
- ||A|| = 3.2076030590345574 — raíz de la suma de a_i²
- ||B|| = 2.5322661173295575 — raíz de la suma de b_i²
- cos θ = 0.9348624996401791 — 7.5934 / (3.2076 · 2.5323)
- θ = 20.794057833961126 — arccos(cos θ) en grados
- d(A, B) = 1.2305442983400008 — raíz de la suma de (a_i - b_i)²

## Coseno entre palabras en GloVe

| palabra 1 | palabra 2 | coseno |
|---|---|---|
| assessment | assessments | 0.8292 |
| assessment | evaluation | 0.8064 |
| generative | ai | 0.0447 |
| higher | tertiary | 0.3624 |
| students | teachers | 0.8670 |
| plagiarism | cheating | 0.6324 |
| integrity | plagiarism | 0.1936 |

## Resumen y contexto en el corpus

| algoritmo | tipo | sim(A,B) | media 190 pares | mín | máx | puesto de (A,B) | ms por par |
|---|---|---|---|---|---|---|---|
| levenshtein | CLASICO | 0.2985 | 0.2809 | 0.2334 | 0.3206 | 48 de 190 | 4.122 |
| needleman-wunsch | CLASICO | 0.0684 | 0.0530 | 0.0150 | 0.1102 | 44 de 190 | 0.310 |
| jaccard | CLASICO | 0.0863 | 0.0914 | 0.0278 | 0.1628 | 104 de 190 | 0.163 |
| coseno-tfidf | CLASICO | 0.1864 | 0.1016 | 0.0205 | 0.3001 | 15 de 190 | 0.192 |
| embeddings-oracion | IA | 0.7355 | 0.5646 | 0.3540 | 0.8211 | 9 de 190 | 4.917 |
| embeddings-palabras | IA | 0.9349 | 0.9583 | 0.9237 | 0.9848 | 179 de 190 | 0.020 |

Correlación de Spearman entre algoritmos sobre los 190 pares:

| | levenshtein | needleman-wunsch | jaccard | coseno-tfidf | embeddings-oracion | embeddings-palabras |
|---|---|---|---|---|---|---|
| levenshtein | 1.00 | 0.48 | 0.13 | 0.30 | 0.23 | 0.23 |
| needleman-wunsch | 0.48 | 1.00 | 0.43 | 0.66 | 0.38 | 0.43 |
| jaccard | 0.13 | 0.43 | 1.00 | 0.62 | 0.38 | 0.49 |
| coseno-tfidf | 0.30 | 0.66 | 0.62 | 1.00 | 0.66 | 0.54 |
| embeddings-oracion | 0.23 | 0.38 | 0.38 | 0.66 | 1.00 | 0.55 |
| embeddings-palabras | 0.23 | 0.43 | 0.49 | 0.54 | 0.55 | 1.00 |
