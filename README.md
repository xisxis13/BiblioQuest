## Lien discord: https://discord.gg/dtbe54kw

### Start docker config:
```docker compose up --build```
### Stop docker config:
```docker compose down```
### Connecting to db
#### PgAdmin 4
- install pgAdmin 4
    - windows: https://www.postgresql.org/ftp/pgadmin/pgadmin4/v9.10/windows/
    - macOS: https://www.postgresql.org/ftp/pgadmin/pgadmin4/v9.10/macos/
- register database server
<p align="center">
  <img src="img/registerServer.png" alt="Synth Interface Preview" width="600"/>
</p>
<p align="center">
    <img src="img/registerServerGeneral.png" alt="Synth Interface Preview" width="600"/>
</p>
    <ul>
        <li>
            <strong>hostname:</strong> bookspg.postgres.database.azure.com
        </li>
        <li>
            <strong>port:</strong> 5432
        </li>
        <li>
            <strong>username:</strong> elmos
        </li>
        <li>
            <strong>password:</strong> booksPrj123
        </li>
    </ul>
<p align="center">
    <img src="img/registerServerConnection.png" alt="Synth Interface Preview" width="600"/>
</p>

- click save

