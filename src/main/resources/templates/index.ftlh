<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>URL Shortener</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">

    <style>
        html, body {
            margin: 0;
        }

        html {
            height: calc(100% - 8px);
        }

        body {
            min-height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;

            font-family: Roboto, sans-serif;

            margin: 8px;
        }

        .d-none {
            display: none !important;
        }

        .main {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 14px;
            max-width: 100%;
        }

        .short-form {
            display: flex;
            flex-direction: row;
            max-width: 100%;
            width: 300px;
        }

        .short-form__input {
            flex: 1;
            font-family: Roboto, sans-serif;
            padding: 10px 14px;
            border: 2px solid #2196f3;
            border-right-width: 0;
            border-radius: 20px 0 0 20px;
            max-width: 100%;
            min-width: 10px;
        }

        .short-form__input:focus-visible {
            outline: none;
        }

        .short-form__input::placeholder {
            color: #9e9e9e;
        }

        .short-form__button {
            font-family: Roboto, sans-serif;
            font-size: 0.9rem;
            cursor: pointer;

            border: none;
            padding: 3px 15px;
            background: #2196f3;
            border-radius: 0 20px 20px 0;
            color: #e3f2fd;
        }

        .short-url {
            text-decoration: none;
            color: #039be5;
            font-size: 1.2rem;

            overflow-x: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            max-width: 100%;
            padding: 0 2px;
        }
    </style>
</head>
<body>
    <main class="main">
        <form id="short-form" class="short-form">
            <input type="url" name="url" required class="short-form__input" placeholder="https://example.com/">
            <button type="submit" class="short-form__button">Shorten</button>
        </form>

        <a href="" class="short-url d-none" id="short-url" target="_blank" rel="noopener"></a>
    </main>
    <script>
        const shortForm = document.getElementById('short-form');
        const shortUrl = document.getElementById('short-url');

        const isValidHttpUrl = string => {
            let url;
            try {
                url = new URL(string);
            } catch (_) {
                return false;
            }
            return url.protocol === "http:" || url.protocol === "https:";
        };

        const setResultUrl = (url) => {
            if (url) {
                shortUrl.href = url;
                shortUrl.textContent = url;
                shortUrl.classList.remove('d-none');
            } else {
                shortUrl.classList.add('d-none');
            }
        };

        shortForm.addEventListener('submit', event => {
            event.preventDefault();

            const data = new FormData(event.currentTarget);
            /** @type {string} */
            const url = data.get('url');

            if (!isValidHttpUrl(url)) {
                return;
            }

            fetch('/api/shorten?' + new URLSearchParams({
                url
            }))
                .then(response => response.text())
                .then(shortUrl => {
                    setResultUrl(shortUrl);
                });
        });
    </script>
</body>
</html>