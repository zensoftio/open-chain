INSERT INTO blocks (id, TIMESTAMP, height, previous_hash, hash, signature, public_key)
VALUES (1,
        1532345018021,
        1,
        '',
        '838c84179c7e644cdf2ff0af3055ed45c6f43e0bd7634f8bd6ae7d088b1aaf0a',
        'MEUCIQCLeQuqCrDd8nmS037ZfmQNtpUf/AsfQilmK7CcNNIi7QIgKNdhszih/PezHW52v4/tdsZxaLovJzDnLvUy98tnsgg=',
        '038bbbeeb867b999991cd5b146b392ba2fe44ea69d1cc7208e32190184b13aaf1b');

INSERT INTO genesis_blocks (id, epoch_index)
VALUES (1, 1);

INSERT INTO wallets (id, address, balance)
VALUES (1, '0x0000000000000000000000000000000000000000', 10000000000000000);

INSERT INTO delegates (id, public_key, address, host, port, registration_date)
VALUES (1, '02b04aa1832e799503000a6b8da1cdbb737c167fc829472c726a12ad9a4ccf24eb', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9190, 1535618817165),
       (2, '02c6847fcdc0239581151d1b05e7004c331eba097ae381349220c7cb0c5f5df9b3', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9191, 1535618817165),
       (3, '02c4aedc4a7e2d8cc0e73e6dfb428e8555adc8a1b74207cb61143babd3e04be63f', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9192, 1535618817165),
       (4, '02203492b48445da0f7392f6fa88d902f71d1b3714740ed99f43009a70fd7f6da8', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9193, 1535618817165),
       (5, '029a9b6a44d2e322af6884a00660d63ab80effceb0a80f86bd7b21fbf5ee1550ac', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9194, 1535618817165),
       (6, '020c08e5367fd881e52af43532db814d371b6bd3effb14442ad1044e71c0c0e41a', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9195, 1535618817165),
       (7, '02aef406b4c4a3c007094a05c2d2a2d815133a41914c96385a2d9ca71529b4d302', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9196, 1535618817165),
       (8, '03bfcc7afddf4f00c043faca2254ca8f09e3109c20b830d44a9b4438b363b9865e', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9197, 1535618817165),
       (9, '03b49d9a127c271fad4bcdf88bd9fb3430b122044972654dfe78a754c5e3064f4f', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9198, 1535618817165),
       (10, '03679e387bae8b7b724edc42a8149b7aa426edfc9ad54a1fc5e717ab081aca4daf', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9199, 1535618817165),
       (11, '036a1a1a6e952083beb1eb5213168288592cd000b42502bd4b8b1e74a465a2eacc', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.194.73.134', 9200, 1535618817165),
       (12, '029137a16dcea3967e8fd46dff0d812a2e60a57bef3eb6a7007867c0496631c5d6', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.199.41.146', 9190, 1535618817165),
       (13, '03a9623189c1da22cec1338d2ab0a982e51794aefb45107d7c4c000a09fc772204', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.199.41.146', 9191, 1535618817165),
       (14, '0283d909d2a886e9274f76f0460625e72674222b6a2bc937071858aa76a6e08d78', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.199.41.146', 9192, 1535618817165),
       (15, '02f8f3aca6fbf37e7dfd4cf55cf6a1dcffa2cc6cb0c2e513f8121dfb4d861bf04e', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.199.41.146', 9193, 1535618817165),
       (16, '039745d56241820f2a385c77aca013ecdff0b9fdce01d3f45ed34752cc9aa62cda', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.199.41.146', 9194, 1535618817165),
       (17, '02e7cb6589255a6e153d181c19aa8a34c5c0e6cef0c0374e0c8ba4b5f36ccfc18a', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.199.41.146', 9195, 1535618817165),
       (18, '02c2aca26e916926fce4101f0633009ae1c8c97e3081b3779880f6683ea258599c', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.199.41.146', 9196, 1535618817165),
       (19, '027d26f614afe8b6b3c8efb861c6666985701d76efa70c9d5a02f44c1e0be804ab', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.199.41.146', 9197, 1535618817165),
       (20, '0225aebdbb8ea2d8c401a638c87da670d5e2f0e4fdb9197f09ae75b2c805046724', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.199.41.146', 9198, 1535618817165),
       (21, '02e20add31fbf82b1369e8f2f537df9225a05d6fd497e2f9c65ee9b2df176c01c8', '0x51c5311F25206De4A9C6ecAa1Bc2Be257B0bA1fb', '35.199.41.146', 9199, 1535618817165);

INSERT INTO delegate2genesis (delegate_id, genesis_id)
VALUES (1, 1),
       (2, 1),
       (3, 1),
       (4, 1),
       (5, 1),
       (6, 1),
       (7, 1),
       (8, 1),
       (9, 1),
       (10, 1),
       (11, 1),
       (12, 1),
       (13, 1),
       (14, 1),
       (15, 1),
       (16, 1),
       (17, 1),
       (18, 1),
       (19, 1),
       (20, 1),
       (21, 1);