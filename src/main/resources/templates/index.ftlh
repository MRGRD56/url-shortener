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

            min-width: 250px;
        }

        .d-none {
            display: none !important;
        }

        .main {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 20px;
            max-width: 100%;
        }

        .short-form {
            display: flex;
            flex-direction: column;
            gap: 8px;

            max-width: 100%;
        }

        .short-form-component {
            display: flex;
            flex-direction: row;
            max-width: 100%;
            width: 600px;
            position: relative;

            height: 60px;
            max-height: 60px;
        }

        .short-form__input:focus, .short-form-component:hover > .short-form__input {
            box-shadow: 1px 2px 8px rgb(17 17 17 / 15%);
            border: 1px solid transparent;
        }

        .short-form__input {
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;

            border: 1px solid #d8d8d8;
            border-radius: 12px;

            font-family: Roboto, sans-serif;
            font-size: 1rem;
            padding-left: 25px;
            padding-right: 125px;
            max-width: 100%;
            min-width: 10px;
            height: calc(100% - 4px);
            width: calc(100% - 152px);

            transition: all ease-in-out 0.1s;
        }

        .short-form__input-no-button {
            padding-right: 25px;
            width: calc(100% - 52px);
        }

        .short-form__input:focus-visible {
            outline: none;
        }

        .short-form__input::placeholder {
            color: #9e9e9e;
        }

        .short-form__button {
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;

            font-family: Roboto, sans-serif;
            font-weight: 500;
            font-size: 1rem;
            cursor: pointer;

            border: none;
            margin: 7px;
            padding: 0 25px;
            background: #2196f3;
            border-radius: 8px;
            color: #e3f2fd;

            transition: all ease-in-out 0.1s;
        }

        .short-form__button:hover, .short-form__button:focus {
            background: #1976d2;
        }

        .short-form__button:focus, .short-form__button:focus-visible {
            outline: none;
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
            <div class="short-form-component">
                <input aria-label="URL" type="url" name="url" required class="short-form__input" placeholder="Enter long url">
                <button type="submit" class="short-form__button">Shorten</button>
            </div>
            <div class="short-form-component d-none" id="short-form-short-url">
                <input aria-label="Short URL" type="text" name="shortUrl" class="short-form__input short-form__input-no-button" placeholder="Short URL (not required)">
            </div>
        </form>

        <a href="" class="short-url d-none" id="short-url" target="_blank" rel="noopener"></a>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/dist/js.cookie.min.js"></script>
    <script>
        const IS_AUTHORIZED = Cookies.get('authorized') === 'true';

        const shortForm = document.getElementById('short-form');
        const shortUrl = document.getElementById('short-url');
        const shortFormShortUrl = document.getElementById('short-form-short-url');

        if (IS_AUTHORIZED) {
            shortFormShortUrl.classList.remove('d-none');
        }

        const isValidHttpUrl = string => {
            let url;
            try {
                url = new URL(string);
            } catch (e) {
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
            const shortUrl = IS_AUTHORIZED ? data.get('shortUrl') : undefined;

            if (!isValidHttpUrl(url)) {
                return;
            }

            const params = {url};
            if (shortUrl) {
                params.shortUrl = shortUrl;
            }

            fetch('/api/shorten?' + new URLSearchParams(params))
                .then(response => response.text())
                .then(shortUrl => {
                    setResultUrl(shortUrl);
                });
        });
    </script>
</body>
</html>
